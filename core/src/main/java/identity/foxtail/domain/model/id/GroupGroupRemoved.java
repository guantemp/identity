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
package identity.foxtail.domain.model.id;


import event.foxtail.alpha.domain.model.DomainEvent;

import java.time.LocalDateTime;

/***
 * @author <a href="www.fixtail.cc/authors/guan xianghuan">guan xiangHuan</a>
 * @since JDK8.0
 * @version 0.0.2 2018-01-22
 */
//@JsonAutoDetect(fieldVisibility = Visibility.ANY)
public class GroupGroupRemoved implements DomainEvent {
    private String id;
    private String nestedGroupId;
    private LocalDateTime occurredOn;
    private int version;

    public GroupGroupRemoved(String id, String nestedGroupId) {
        this.version = 1;
        this.occurredOn = LocalDateTime.now();
        this.id = id;
        this.nestedGroupId = nestedGroupId;
    }

    public String id() {
        return id;
    }

    public String nestedGroupId() {
        return nestedGroupId;
    }

    @Override
    public LocalDateTime occurredOn() {
        return occurredOn;
    }

    @Override
    public int version() {
        return version;
    }
}
