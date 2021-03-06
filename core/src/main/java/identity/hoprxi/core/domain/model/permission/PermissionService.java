/*
 * Copyright (c) 2019 www.hoprxi.com All rights Reserved.
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

package identity.hoprxi.core.domain.model.permission;

import java.util.Collection;
import java.util.Objects;

/***
 * @author <job href="www.hoprxi.com/authors/guan xiangHuan">guan xiangHuan</job>
 * @since JDK8.0
 * @version 0.0.1 2019/2/11
 */
public class PermissionService {
    private PermissionRepository repo;

    public PermissionService(PermissionRepository repo) {
        this.repo = Objects.requireNonNull(repo, "repo is required");
    }

    public boolean isSuitableEngineName(String engineName) {
        Collection<String> collection = repo.getNonRepetitivePermissionName();
        return collection.contains(engineName);
    }
}
