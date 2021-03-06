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
package identity.hoprxi.core.domain.servers;

import identity.hoprxi.core.domain.model.id.*;
import identity.hoprxi.core.infrastructure.persistence.ArangoDBSocializationRepository;

import java.util.Optional;

/***
 * @author <a href="www.hoprxi.com/authors/guan xiangHuan">guan xiangHuan</a>
 * @since JDK8.0
 * @version 0.0.1 2020-12-18
 */
public class AuthenticationService {
    private UserRepository userRepository;
    private SocializationRepository socializationRepository = new ArangoDBSocializationRepository("identity");

    public AuthenticationService(UserRepository userRepository) {
        super();
        this.userRepository = userRepository;
    }

    /**
     * @param username
     * @param password
     * @return
     */
    public UserDescriptor authenticateByUsernameAndPassword(String username, String password) {
        User user = userRepository.usernameAuthenticCredentials(username, password);
        return Optional.ofNullable(user).map(User::isAvailable).map(d -> user.toUserDescriptor()).orElse(UserDescriptor.NullUserDescriptor);
    }

    public UserDescriptor authenticateByTelAndPassword(String tel, String password) {
        User user = userRepository.telephoneNumberAuthenticCredentials(tel, password);
        return Optional.ofNullable(user).map(User::isAvailable).map(d -> user.toUserDescriptor()).orElse(UserDescriptor.NullUserDescriptor);
    }

    public UserDescriptor authenticateByTelAndSmsCode(String tel) {
        User user = userRepository.findByTelephoneNumber(tel);
        return Optional.ofNullable(user).map(User::isAvailable).map(d -> user.toUserDescriptor()).orElse(UserDescriptor.NullUserDescriptor);
    }

    public UserDescriptor authenticateByEmailAndPassword(String email, String password) {
        User user = userRepository.emailAuthenticCredentials(email, password);
        return Optional.ofNullable(user).map(User::isAvailable).map(d -> user.toUserDescriptor()).orElse(UserDescriptor.NullUserDescriptor);
    }

    public UserDescriptor authenticateByThirdParty(String unionId) {
        Socialization socialization = socializationRepository.find(unionId);
        return Optional.ofNullable(socialization).map(u -> {
            User user = userRepository.find(socialization.userId());
            return user.toUserDescriptor();
        }).orElseGet(() -> UserDescriptor.NullUserDescriptor);
    }
}
