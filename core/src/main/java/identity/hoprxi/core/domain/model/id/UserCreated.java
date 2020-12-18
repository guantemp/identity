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

import event.hoprxi.domain.model.DomainEvent;

import java.time.LocalDateTime;

/***
 * @author <a href="www.hoprxi.com/authors/guan xianghuang">guan xiangHuan</a>
 * @since JDK8.0
 * @version 0.0.2 20180411
 */
public class UserCreated implements DomainEvent {
    private String id;
    private String telephoneNumber;
    private String email;
    private LocalDateTime occurredOn;
    private String username;
    private Enablement enablement;
    private int version;

    public UserCreated(String id, String username, String telephoneNumber, String email, Enablement enablement) {
        this.id = id;
        this.username = username;
        this.telephoneNumber = telephoneNumber;
        this.email = email;
        this.occurredOn = LocalDateTime.now();
        this.enablement = enablement;
        this.version = 1;
    }

    public String telephoneNumber() {
        return telephoneNumber;
    }

    public String email() {
        return email;
    }

    @Override
    public LocalDateTime occurredOn() {
        return occurredOn;
    }

    public String username() {
        return username;
    }

    @Override
    public int version() {
        return version;
    }

    public String id() {
        return id;
    }

    public Enablement enablement() {
        return enablement;
    }
}
