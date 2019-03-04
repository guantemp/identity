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
import identity.foxtail.core.domain.model.permission.Permission;
import identity.foxtail.core.domain.model.permission.PermissionRepository;
import identity.foxtail.core.domain.model.permission.Result;
import identity.foxtail.core.domain.model.permission.VariantContext;

import java.util.Objects;

/***
 * @author <a href="www.foxtail.cc/authors/guan xiangHuan">guan xiangHuan</a>
 * @since JDK8.0
 * @version 0.0.1 2019-01-28
 */
public class AuthorizationService {
    private UserRepository userRepository;
    private PermissionRepository permissionRepository;
    private RoleRepository roleRepository;
    private GroupMemberService groupMemberService;

    public AuthorizationService(UserRepository userRepository, PermissionRepository permissionRepository, RoleRepository roleRepository, GroupMemberService groupMemberService) {
        this.userRepository = Objects.requireNonNull(userRepository, "userRepository is required");
        this.permissionRepository = Objects.requireNonNull(permissionRepository, "permissionRepository is required");
        this.roleRepository = Objects.requireNonNull(roleRepository, "roleRepository is required");
        this.groupMemberService = Objects.requireNonNull(groupMemberService, "groupMemberService is required");
    }

    /**
     * @param userId
     * @param permissionName
     * @param resourceId
     * @param context
     * @return
     */
    public Result authorization(String userId, String permissionName, String resourceId, VariantContext context) {
        int count = roleRepository.count();
        Role[] roles = roleRepository.all(0, count);
        User user = userRepository.find(userId);
        for (Role role : roles) {
            if (role.isUserInRole(user, groupMemberService)) {
                Permission[] permissions = permissionRepository.findPermissionsWithRoleAndPermissionNameAndResource(role.id(), permissionName, resourceId);
                for (Permission permission : permissions) {
                    if (!permission.isInSchedule())
                        continue;
                    context.put("fuel", permission.processor().fuel());
                    return permission.execute(context);
                }
            }
        }
        return Result.FORBIDDEN;
    }

    /**
     * @param userId
     * @param permissionName
     * @param resourceId
     * @return
     */
    public Result authorization(String userId, String permissionName, String resourceId) {
        return authorization(userId, permissionName, resourceId, new VariantContext());
    }


    public Result backtrackingCategoryauthorization(String userId, String permissionName, String resourceId, String categoryId) {
        return null;
    }
}
