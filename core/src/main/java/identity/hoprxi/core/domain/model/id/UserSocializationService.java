/*
 * Copyright (c) 2020 www.hoprxi.com All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package identity.hoprxi.core.domain.model.id;

import identity.hoprxi.core.domain.model.DomainRegistry;
import identity.hoprxi.core.domain.servers.PasswordService;
import identity.hoprxi.core.infrastructure.persistence.ArangoDBSocializationRepository;
import identity.hoprxi.core.infrastructure.persistence.ArangoDBUserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * @author <a href="www.hoprxi.com/author/guan xianghuang">guan xiangHuan</a>
 * @version 0.0.1 2020-12-14
 * @since JDK8.0
 */
public class UserSocializationService {
    private UserRepository userRepository = new ArangoDBUserRepository("identity");
    private SocializationRepository socializationRepository = new ArangoDBSocializationRepository("identity");
    private static final PasswordService passwordService = new PasswordService();

    public UserDescriptor registerUser(String username, String password, String telephoneNumber, String email, boolean enable, LocalDateTime deadline) {
        User user = userRepository.findByUsername(username);
        if (user != null)
            throw new UsernameExistsException("{code:40001, message:\"用户名已存在\"}");
        if (password == null || password.isEmpty())
            password = passwordService.generateStrongPassword();
        Enablement enablement = Enablement.getInstance(enable, deadline);
        user = new User(userRepository.nextIdentity(), username, password, telephoneNumber, email, enablement);
        userRepository.save(user);
        DomainRegistry.domainEventPublisher().publish(new UserCreated(user.id(), username, telephoneNumber, email, enablement));
        return user.toUserDescriptor();
    }

    public UserDescriptor bindUser(String username, String unionId, String thirdPartyName) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            user = new User(userRepository.nextIdentity(), username, passwordService.generateStrongPassword(),
                    null, null, Enablement.PERMANENCE);
            userRepository.save(user);
            DomainRegistry.domainEventPublisher().publish(new UserCreated(user.id(), username,
                    null, null, Enablement.PERMANENCE));
        }
        Socialization socialization = socializationRepository.find(unionId);
        if (socialization == null) {
            socialization = new Socialization(unionId, user.id(), Socialization.ThirdParty.valueOf(thirdPartyName));
            socializationRepository.save(socialization);
            DomainRegistry.domainEventPublisher().publish(new SocializationBoundUser(unionId, user.id(), thirdPartyName));
        }
        return user.toUserDescriptor();
    }

    public void unbindUser(String unionId) {
        Socialization socialization = socializationRepository.find(unionId);
        if (socialization != null) {
            socializationRepository.remove(unionId);
            DomainRegistry.domainEventPublisher().publish(new SocializationUnboundUser(unionId));
        }
    }

    public UserDescriptor getSocializationBindUser(String unionId) {
        Socialization socialization = socializationRepository.find(unionId);
        return Optional.ofNullable(socialization).map(u -> {
            User user = userRepository.find(socialization.userId());
            return user.toUserDescriptor();
        }).orElseGet(() -> UserDescriptor.NullUserDescriptor);
    }
}
