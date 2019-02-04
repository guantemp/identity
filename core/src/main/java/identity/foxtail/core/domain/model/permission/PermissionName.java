/*
 * Copyright (c) 2019 www.foxtail.cc All rights Reserved.
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

package identity.foxtail.core.domain.model.permission;

import java.util.Objects;

/***
 * @author <job href="www.foxtail.cc/authors/guan xiangHuan">guan xiangHuan</job>
 * @since JDK8.0
 * @version 0.0.1 2019/2/2
 */
public class PermissionName {
    private String name;

    public PermissionName(String name) {
        this.name = name;
    }

    private void setName(String name) {
        name = Objects.requireNonNull(name, "name is required").trim();
        if (name.isEmpty() || name.length() >= 255)
            throw new IllegalArgumentException("name length is [1-256]");
        this.name = name;
    }

    public String name() {
        return name;
    }
}
