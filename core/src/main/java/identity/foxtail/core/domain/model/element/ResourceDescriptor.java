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

import java.util.StringJoiner;

/***
 * @author <a href="www.foxtail.cc/author/guan xianghuang">guan xiangHuan</a>
 * @since JDK8.0
 * @version 0.0.1 20180129
 */
public class ResourceDescriptor {
    private String id;
    private String name;

    protected ResourceDescriptor(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String id() {
        return id;
    }


    public String name() {
        return name;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", ResourceDescriptor.class.getSimpleName() + "[", "]")
                .add("id='" + id + "'")
                .add("name='" + name + "'")
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ResourceDescriptor that = (ResourceDescriptor) o;

        return id != null ? id.equals(that.id) : that.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
