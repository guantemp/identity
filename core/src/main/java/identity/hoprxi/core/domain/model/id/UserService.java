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

import java.util.Objects;

/***
 * @author <a href="www.hoprxi.com/authors/guan xiangHuan">guan xiangHuan</a>
 * @since JDK8.0
 * @version 0.0.2 2020-12-02
 */
public class UserService {
    private UserRepository repository;

    /**
     * @param repository
     */
    public UserService(UserRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository is required");
    }

    /**
     * @param id
     * @param username
     * @param password
     * @param cellphoneNumber
     * @param enablement
     * @return
     */
    public UserDescriptor registerUser(String id, String username, String password, String cellphoneNumber, Enablement enablement) {
        User user = repository.find(id);
        if (user != null)
            return UserDescriptor.NullUserDescriptor;
        if (repository.isUsernameExists(username))
            return UserDescriptor.NullUserDescriptor;
        if (repository.isTelephoneNumberExists(cellphoneNumber))
            return UserDescriptor.NullUserDescriptor;
        user = new User(id, username, password, cellphoneNumber, enablement);
        repository.save(user);
        DomainRegistry.domainEventPublisher().publish(new UserCreated(id, username, cellphoneNumber, enablement));
        return user.toUserDescriptor();
    }

    /**
     * @param username
     * @param password
     * @return
     */
    public UserDescriptor authenticate(String username, String password) {
        User user = repository.usernameAuthenticCredentials(username, password);
        if (user == null)
            return UserDescriptor.NullUserDescriptor;
        if (user.isEnable())
            return user.toUserDescriptor();
        return UserDescriptor.NullUserDescriptor;
    }


}
