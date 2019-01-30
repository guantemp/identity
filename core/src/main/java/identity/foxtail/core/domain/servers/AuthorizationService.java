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

package identity.foxtail.core.domain.servers;

import identity.foxtail.core.domain.model.element.Role;
import identity.foxtail.core.domain.model.element.RoleRepository;
import identity.foxtail.core.domain.model.id.GroupMemberService;
import identity.foxtail.core.domain.model.id.User;
import identity.foxtail.core.domain.model.id.UserRepository;
import identity.foxtail.core.domain.model.permission.PermissionRepository;
import identity.foxtail.core.infrastructure.persistence.ArangoDBGroupRepository;
import identity.foxtail.core.infrastructure.persistence.ArangoDBPermissionRepository;
import identity.foxtail.core.infrastructure.persistence.ArangoDBRoleRepository;
import identity.foxtail.core.infrastructure.persistence.ArangoDBUserRepository;

/***
 * @author <a href="www.foxtail.cc/authors/guan xiangHuan">guan xiangHuan</a>
 * @since JDK8.0
 * @version 0.0.1 2019-01-28
 */
public class AuthorizationService {
    private UserRepository userRepository = new ArangoDBUserRepository();
    private PermissionRepository permissionRepository = new ArangoDBPermissionRepository();
    private RoleRepository roleRepository = new ArangoDBRoleRepository();
    private GroupMemberService groupMemberService = new GroupMemberService(new ArangoDBGroupRepository());

    public boolean authorization(String userId, String permission) {
        int count = roleRepository.count();
        Role[] roles = roleRepository.all(0, count - 1);
        User user = userRepository.find(userId);
        for (Role role : roles) {
            if (role.isUserInRole(user, groupMemberService)) {

            }
        }
        return true;
    }
}
