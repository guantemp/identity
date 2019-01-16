/*
 *  Copyright 2018 www.foxtail.cc All rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */
package identity.foxtail.core.domain.servers;

import identity.foxtail.core.domain.model.id.User;
import identity.foxtail.core.domain.model.id.UserDescriptor;
import identity.foxtail.core.domain.model.id.UserRepository;

/***
 * @author <a href="www.foxtail.cc/authors/guan xiangHuan">guan xiangHuan</a>
 * @since JDK8.0
 * @version 0.0.1 20180106
 */
public final class AuthenticationService {
    private UserRepository repository;

    public AuthenticationService(UserRepository repository) {
        super();
        this.repository = repository;
    }

    /**
     * @param username
     * @param password
     * @return
     */
    public UserDescriptor authenticate(String username, String password) {
        User user = repository.usernameAuthenticCredentials(username, password);
        if (user != null && user.isEnable())
            return user.toUserDescriptor();
        return UserDescriptor.NullUserDescriptor;
    }
}
