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
package identity.foxtail.core.domain.model.element;

/***
 * @author <a href="www.foxtail.cc/author/guan xianghuang">guan xiangHuan</a>
 * @since JDK8.0
 * @version 0.0.2 20180-11-25
 */
public interface ResourceRepository {
    /**
     * @param id
     * @return
     */
    Resource find(String id);

    /**
     * @param resource
     */
    void save(Resource resource);

    /**
     * @return
     */
    Resource[] root();

    /**
     * @param id
     * @return
     */
    Resource[] children(String id);

    /**
     * @param id
     * @return
     */
    boolean hasChildren(String id);

    /**
     * @param id Will not be deleted ,if the has children
     */
    void remove(String id);

    /**
     * @param id
     * @return null if it's root
     */
    Resource parent(String id);
}
