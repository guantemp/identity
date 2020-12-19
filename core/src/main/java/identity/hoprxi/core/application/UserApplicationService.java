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

import identity.hoprxi.core.application.command.ChangeUsernameCommand;
import identity.hoprxi.core.application.command.RegisterUserCommand;
import identity.hoprxi.core.application.command.ResetUserPasswordCommand;
import identity.hoprxi.core.application.command.SocializationBindUserCommand;
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
public class UserApplicationService {
    private UserRepository userRepository = new ArangoDBUserRepository("identity");
    private UserSocializationService userSocializationService = new UserSocializationService();
    private AuthenticationService authenticationService = new AuthenticationService(userRepository);

    /**
     * @param registerUserCommand
     */
    public UserDescriptor registerUser(RegisterUserCommand registerUserCommand) {
        UserDescriptor userDescriptor = userSocializationService.registerUser(
                registerUserCommand.getUsername(), registerUserCommand.getPassword(),
                registerUserCommand.getTelephoneNumber(), registerUserCommand.getEmail(),
                registerUserCommand.isEnable(), registerUserCommand.getDeadline());
        return userDescriptor;
    }

    public UserDescriptor authenticate(String usernameOrTelOrEmail, String password) {
        UserDescriptor userDescriptor = authenticationService.authenticateByUsernameAndPassword(usernameOrTelOrEmail, password);
        if (userDescriptor != UserDescriptor.NullUserDescriptor)
            return userDescriptor;
        userDescriptor = authenticationService.authenticateByTelAndPassword(usernameOrTelOrEmail, password);
        if (userDescriptor != UserDescriptor.NullUserDescriptor)
            return userDescriptor;
        return authenticationService.authenticateByEmailAndPassword(usernameOrTelOrEmail, password);
    }

    public UserDescriptor authenticateBySmsCode(String tel, String smsCode) {
        return authenticationService.authenticateByTelAndSmsCode(tel);
    }

    public UserDescriptor authenticateByThirdParty(String unionId) {
        return authenticationService.authenticateByThirdParty(unionId);
    }

    public UserDescriptor socializationBindUser(SocializationBindUserCommand socializationBindUserCommand) {
        return userSocializationService.socializationBindUser(socializationBindUserCommand.getUsername(), socializationBindUserCommand.getUnionId(), socializationBindUserCommand.getThirdPartyName());
    }

    public void resetUserPassword(ResetUserPasswordCommand resetUserPasswordCommand) {
        User user = existingUser(resetUserPasswordCommand.getUserId());
        user.resetPassword(resetUserPasswordCommand.getNewPassword());
    }

    public void changeUsername(ChangeUsernameCommand changeUsernameCommand) {
        User user = existingUser(changeUsernameCommand.getUserId());
        user.rename(changeUsernameCommand.getUsername());
    }

    private User existingUser(String userId) {
        User user = userRepository.find(userId);
        if (user == null)
            throw new IllegalArgumentException("User does not exist for: " + userId);
        return user;
    }
}
