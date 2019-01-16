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
package identity.foxtail.core.domain.model.id;

import event.foxtail.alpha.domain.model.DomainEvent;

import java.time.LocalDateTime;

/***
 * @author <a href="www.foxtail.cc/authors/guan xiangHuan">guan xiangHuan</a>
 * @since JDK8.0
 * @version 0.0.1 20180224
 */
public class GroupUserRemoved implements DomainEvent {
    private String id;
    private LocalDateTime occurredOn;
    private String userId;
    private int version;

    public GroupUserRemoved(String id, String userId) {
        this.version = 1;
        this.occurredOn = LocalDateTime.now();
        this.id = id;
        this.userId = userId;
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

