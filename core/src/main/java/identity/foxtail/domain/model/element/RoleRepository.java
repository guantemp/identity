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
package identity.foxtail.domain.model.element;

/***
 * @author <a href="www.foxtail.cc/authors/guan xiangHuan">guan xiangHuan</a>
 * @since JDK8.0
 * @version 0.0.2 20180122
 */
public interface RoleRepository {
    /**
     * @return
     */
    Role[] all(int offset, int limit);

    /**
     * @return
     */
    String nextIdentity();

    /**
     * @param id
     */
    void remove(String id);

    /**
     * @param id
     * @return
     */
    Role find(String id);

    /**
     * @param role
     */
    void save(Role role);

    /**
     * @return
     */
    int count();
}
