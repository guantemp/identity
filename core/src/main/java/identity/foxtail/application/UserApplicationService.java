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
package identity.foxtail.application;

import identity.foxtail.core.application.command.ChangeUserPasswordCommand;
import identity.foxtail.core.application.command.ChangeUsernameCommand;
import identity.foxtail.core.application.command.RegisterUserCommand;
import identity.foxtail.core.domain.model.id.*;
import identity.foxtail.core.domain.servers.PasswordService;
import identity.foxtail.core.infrastructure.persistence.ArangoDBUserRepository;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * @author <a href="www.foxtail.cc/authors/guan xiangHuan">guan xiangHuan</a>
 * @version 0.0.1 20180411
 * @since JDK8.0
 */
public final class UserApplicationService {
    private static final int EXPIRED_DAYS = 45;
    private static final PasswordService passwordService = new PasswordService();
    private UserRepository userRepository = new ArangoDBUserRepository();
    private UserService userService = new UserService(userRepository);

    /**
     * @param registerUserCommand
     */
    public UserDescriptor registerUser(RegisterUserCommand registerUserCommand) {
        String password = registerUserCommand.getPassword();
        if (password == null)
            password = passwordService.generateStrongPassword();
        UserDescriptor userDescriptor = userService.registerUser(userRepository.nextIdentity(),
                registerUserCommand.getUsername(),
                password,
                registerUserCommand.getNickname(),
                new Enablement(true, LocalDateTime.now().plusDays(EXPIRED_DAYS)));
        return userDescriptor;
    }

    public void changeUserPassword(ChangeUserPasswordCommand changeUserPasswordCommand) {
        User user = existingUser(changeUserPasswordCommand.getCurrentPassword());
        user.changPassword(changeUserPasswordCommand.getCurrentPassword(), changeUserPasswordCommand.getChangedPassword());
    }

    public void changeUsername(ChangeUsernameCommand changeUsernameCommand) {
        User user = existingUser(changeUsernameCommand.getUserId());
        user.rename(changeUsernameCommand.getUsername());
    }

    public UserDescriptor authenticate(String username, String password) {
        username = Objects.requireNonNull(username, "username required").trim();
        if (username.isEmpty())
            throw new IllegalArgumentException("username must can not be empty.");
        password = Objects.requireNonNull(password, "password required");
        return userService.authenticate(username, password);
    }


    private User existingUser(String userId) {
        User user = userRepository.find(userId);
        if (user == null)
            throw new IllegalArgumentException("User does not exist for: " + userId);
        return user;
    }
}
