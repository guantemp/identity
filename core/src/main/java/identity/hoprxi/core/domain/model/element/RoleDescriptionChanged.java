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
package identity.hoprxi.core.domain.model.element;

import event.hoprxi.domain.model.DomainEvent;

import java.time.LocalDateTime;

/***
 * @author <a href="www.hoprxi.com/authors/guan xiangHuan">guan xiangHuan</a>
 * @since JDK8.0
 * @version 0.0.1 20180122
 */
public class RoleDescriptionChanged implements DomainEvent {
    private int version;
    private LocalDateTime occurredOn;
    private String id;
    private String descriptor;

    public RoleDescriptionChanged(String id, String descriptor) {
        this.id = id;
        this.descriptor = descriptor;
        this.occurredOn = LocalDateTime.now();
        this.version = 1;
    }

    public String id() {
        return id;
    }

    public String descriptor() {
        return descriptor;
    }

    @Override
    public LocalDateTime occurredOn() {
        return this.occurredOn;
    }

    @Override
    public int version() {
        return this.version;
    }
}