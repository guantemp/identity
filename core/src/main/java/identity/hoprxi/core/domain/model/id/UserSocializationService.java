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
import java.util.regex.Pattern;

/**
 * @author <a href="www.hoprxi.com/author/guan xianghuang">guan xiangHuan</a>
 * @version 0.0.1 2020-12-14
 * @since JDK8.0
 */
public class UserSocializationService {
    private UserRepository userRepository = new ArangoDBUserRepository("identity");
    private SocializationRepository socializationRepository = new ArangoDBSocializationRepository("identity");
    private static final PasswordService passwordService = new PasswordService();
    private static final Pattern MOBILE_CN_PATTERN = Pattern.compile("^[1](([3][0-9])|([4][5,7,9])|([5][^4,6,9])|([6][6])|([7][3,5,6,7,8])|([8][0-9])|([9][8,9]))[0-9]{8}$");
    private static final Pattern FIXED_TELEPHONE_CN_PATTERN = Pattern.compile("^(0\\d{2}-\\d{8}(-\\d{1,4})?)|(0\\d{3}-\\d{7,8}(-\\d{1,4})?)$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^\\s*\\w+(?:\\.{0,1}[\\w-]+)*@[a-zA-Z0-9]+(?:[-.][a-zA-Z0-9]+)*\\.[a-zA-Z]+\\s*$");

    public UserDescriptor registerUser(String username, String password, String telephoneNumber, String email,
                                       boolean enable, LocalDateTime deadline) {
        Enablement enablement = Enablement.getInstance(enable, deadline);
        if (password == null || password.isEmpty())
            password = passwordService.generateStrongPassword();
        User user = new User(userRepository.nextIdentity(), username, password, telephoneNumber, email, enablement);
        System.out.println(user.username() + "   " + user.telephoneNumber());
        System.out.println(userRepository.isTelephoneNumberExists(user.telephoneNumber()));
        if ((userRepository.isUsernameExists(username) || userRepository.isTelephoneNumberExists(username) || userRepository.isEmailExists(username))
                || (null != user.telephoneNumber() && (userRepository.isTelephoneNumberExists(user.telephoneNumber()) || userRepository.isUsernameExists(user.telephoneNumber())))
                || (null != user.email() && (userRepository.isEmailExists(user.email()) || userRepository.isUsernameExists(user.email())))) {
            throw new UsernameExistsException("{\n" +
                    "\t\"code\": 40001,\n" +
                    "\t\"message\": \"用户名已存在\"\n" +
                    "}");
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
        //exists,do nothing
        if (socialization != null) {
            return UserDescriptor.NullUserDescriptor;
        }
        socialization = new Socialization(unionId, user.id(), Socialization.ThirdParty.valueOf(thirdPartyName));
        socializationRepository.save(socialization);
        DomainRegistry.domainEventPublisher().publish(new SocializationBoundUser(unionId, user.id(), thirdPartyName));
        return user.toUserDescriptor();
    }

    public void socializationUnbindUser(String unionId) {
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
