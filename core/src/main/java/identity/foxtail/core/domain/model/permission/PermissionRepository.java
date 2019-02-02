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

package identity.foxtail.core.domain.model.permission;

/***
 * @author <a href="www.foxtail.cc/authors/guan xiangHuan">guan xiangHuan</a>
 * @since JDK8.0
 * @version 0.0.1 2018-12-03
 */
public interface PermissionRepository {
    /**
     * @param permission
     */
    void save(Permission permission);

    /**
     * @param id
     * @return
     */
    Permission find(String id);


    /**
     * @param roleId
     * @param permissionName
     * @return
     */
    Permission[] findByRoleAndName(String roleId, String permissionName);

    /**
     * @return
     */
    String nextIdentity();
    /**
     * @param id
     */
    void remove(String id);

}
