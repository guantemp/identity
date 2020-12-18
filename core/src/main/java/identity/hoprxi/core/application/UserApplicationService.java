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
package identity.hoprxi.core.application;

import identity.hoprxi.core.application.command.ChangeUserPasswordCommand;
import identity.hoprxi.core.application.command.ChangeUsernameCommand;
import identity.hoprxi.core.application.command.RegisterUserCommand;
import identity.hoprxi.core.domain.model.id.User;
import identity.hoprxi.core.domain.model.id.UserDescriptor;
import identity.hoprxi.core.domain.model.id.UserRepository;
import identity.hoprxi.core.domain.model.id.UserSocializationService;
import identity.hoprxi.core.domain.servers.AuthenticationService;
import identity.hoprxi.core.infrastructure.persistence.ArangoDBUserRepository;

/**
 * @author <a href="www.hoprxi.com/authors/guan xiangHuan">guan xiangHuan</a>
 * @version 0.0.2 2020-12-18
 * @since JDK8.0
 */
public final class UserApplicationService {
    private static final int EXPIRED_DAYS = 45;
    private UserRepository userRepository = new ArangoDBUserRepository("identity");
    private UserSocializationService userSocializationService = new UserSocializationService();
    private AuthenticationService authenticationService = new AuthenticationService(userRepository);

    /**
     * @param registerUserCommand
     */
    public UserDescriptor registerUser(RegisterUserCommand registerUserCommand) {
        if (userRepository.isUsernameExists(registerUserCommand.getUsername()))
            return UserDescriptor.NullUserDescriptor;
        if (userRepository.isTelephoneNumberExists(registerUserCommand.getTelephoneNumber()))
            return UserDescriptor.NullUserDescriptor;
        if (userRepository.isEmailExists(registerUserCommand.getEmail()))
            return UserDescriptor.NullUserDescriptor;
        return userSocializationService.registerUser(
                registerUserCommand.getUsername(), registerUserCommand.getPassword(),
                registerUserCommand.getTelephoneNumber(), registerUserCommand.getEmail(),
                registerUserCommand.isEnable(), registerUserCommand.getDeadline()
        );
    }

    public UserDescriptor authenticate(String username, String password) {
        return authenticationService.authenticateByUsernameAndPassword(username, password);
    }

    public void changeUserPassword(ChangeUserPasswordCommand changeUserPasswordCommand) {
        User user = existingUser(changeUserPasswordCommand.getCurrentPassword());
        user.resetPassword(changeUserPasswordCommand.getChangedPassword());
    }

    public void changeUsername(ChangeUsernameCommand changeUsernameCommand) {
        User user = existingUser(changeUsernameCommand.getUserId());
        user.rename(changeUsernameCommand.getUsername());
    }


    public UserDescriptor authenticate(String username, int smsCode) {

        return null;
    }

    private User existingUser(String userId) {
        User user = userRepository.find(userId);
        if (user == null)
            throw new IllegalArgumentException("User does not exist for: " + userId);
        return user;
    }
}
