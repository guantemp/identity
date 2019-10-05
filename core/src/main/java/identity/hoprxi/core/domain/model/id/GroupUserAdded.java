/*
 *  Copyright 2018 www.hoprxi.com All rights Reserved.
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
package identity.hoprxi.core.domain.model.id;

import event.hoprxi.domain.model.DomainEvent;

import java.time.LocalDateTime;

/***
 * @author <a href="www.hoprxi.com/authors/guan xianghuan</a>
 * @since JDK8.0
 * @version 0.0.1 20180224
 */
//@JsonAutoDetect(fieldVisibility = Visibility.ANY)
public class GroupUserAdded implements DomainEvent {
    private String id;
    private LocalDateTime occurredOn;
    private String userId;
    private int version;

    public GroupUserAdded(String id, String userId) {
        this.id = id;
        this.userId = userId;
        this.version = 1;
        this.occurredOn = LocalDateTime.now();
    }

    public String id() {
        return id;
    }

    @Override
    public LocalDateTime occurredOn() {
        return occurredOn;
    }

    public String userId() {
        return userId;
    }

    @Override
    public int version() {
        return version;
    }
}
