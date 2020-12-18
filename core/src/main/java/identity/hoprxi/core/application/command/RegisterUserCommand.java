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
package identity.hoprxi.core.application.command;

import java.time.LocalDateTime;

/**
 * @author <a href="www.hoprxi.com/authors/guan xiangHuan">guan xiangHuan</a>
 * @version 0.0.2 2020-12-17
 * @since JDK8.0
 */
public class RegisterUserCommand {
    private String username;
    private String telephoneNumber;
    private String email;
    private String password;
    private boolean enable;
    private LocalDateTime deadline;

    public RegisterUserCommand(String username, String telephoneNumber, String email, String password,
                               boolean enable, LocalDateTime deadline) {
        this.username = username;
        this.telephoneNumber = telephoneNumber;
        this.email = email;
        this.password = password;
        this.enable = enable;
        this.deadline = deadline;
    }

    public String getTelephoneNumber() {
        return telephoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public boolean isEnable() {
        return enable;
    }

    public LocalDateTime getDeadline() {
        return deadline;
    }

    public String getUsername() {
        return username;
    }


    public String getPassword() {
        return password;
    }

}
