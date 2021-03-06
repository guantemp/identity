/*
 * Copyright (c) 2021 www.hoprxi.com All Rights Reserved.
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

/**
 * @author <a href="www.hoprxi.com/author/guan xianghuang">guan xiangHuan</a>
 * @version 0.0.1 2020-12-14
 * @since JDK8.0
 */
public class UserSocializationService {
    private UserRepository userRepository = new ArangoDBUserRepository("identity");
    private SocializationRepository socializationRepository = new ArangoDBSocializationRepository("identity");
    private static final PasswordService passwordService = new PasswordService();

    public UserDescriptor registerUser(String username, String password, String telephoneNumber, String email,
                                       boolean enable, LocalDateTime deadline) {
        Enablement enablement = Enablement.getInstance(enable, deadline);
        if (password == null || password.isEmpty())
            password = passwordService.generateStrongPassword();
        User user = new User(userRepository.nextIdentity(), username, password, telephoneNumber, email, enablement);
        if ((userRepository.isUsernameExists(username) || userRepository.isTelephoneNumberExists(username) || userRepository.isEmailExists(username))
                || (null != user.telephoneNumber() && (userRepository.isTelephoneNumberExists(user.telephoneNumber()) || userRepository.isUsernameExists(user.telephoneNumber())))
                || (null != user.email() && (userRepository.isEmailExists(user.email()) || userRepository.isUsernameExists(user.email())))) {
            throw new UsernameExistsException("用户名已存在");
        }
        userRepository.save(user);
        DomainRegistry.domainEventPublisher().publish(new UserCreated(user.id(), username, user.telephoneNumber(), user.email(), enablement));
        return user.toUserDescriptor();
    }

    public UserDescriptor socializationBindUser(String username, String unionId, String thirdPartyName) {
        User user = userRepository.findByUsername(username);
        //not exists,new
        if (user == null) {
            user = new User(userRepository.nextIdentity(), username, passwordService.generateStrongPassword(),
                    null, null, Enablement.PERMANENCE);
            userRepository.save(user);
            DomainRegistry.domainEventPublisher().publish(new UserCreated(user.id(), username, user.telephoneNumber(), user.email(), Enablement.PERMANENCE));
        }
        Socialization socialization = socializationRepository.find(unionId);
        //not exists,bind
        if (socialization == null) {
            socialization = new Socialization(unionId, user.id(), Socialization.ThirdParty.valueOf(thirdPartyName));
            socializationRepository.save(socialization);
            DomainRegistry.domainEventPublisher().publish(new SocializationBoundUser(unionId, user.id(), thirdPartyName));
        }
        return user.toUserDescriptor();
    }

    public void socializationUnbindUser(String unionId) {
        Socialization socialization = socializationRepository.find(unionId);
        if (socialization != null) {
            socializationRepository.remove(unionId);
            DomainRegistry.domainEventPublisher().publish(new SocializationUnboundUser(unionId));
        }
    }

    public void changUserPassword(String username, String newPassword) {
        User user = userRepository.findByUsername(username);
        user.resetPassword(newPassword);
    }
}
