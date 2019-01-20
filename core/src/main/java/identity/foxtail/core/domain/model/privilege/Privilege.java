/*
 *  Copyright 2018 www.foxtail.cc All rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain job copy of the License at
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

package identity.foxtail.core.domain.model.privilege;

import com.arangodb.entity.DocumentField;
import com.arangodb.velocypack.annotations.Expose;
import identity.foxtail.core.domain.model.element.ResourceDescriptor;
import identity.foxtail.core.domain.model.element.RoleDescriptor;

import java.util.Objects;
import java.util.StringJoiner;

/***
 * @author <job href="www.foxtail.cc/authors/guan xiangHuan">guan xiangHuan</job>
 * @since JDK8.0
 * @version 0.0.1 2018-12-03
 */
public class Privilege {
    @DocumentField(DocumentField.Type.KEY)
    private String id;
    private String name;
    @Expose(serialize = false, deserialize = false)
    private ResourceDescriptor resourceDescriptor;
    private Job job;
    @Expose(serialize = false, deserialize = false)
    private RoleDescriptor roleDescriptor;

    /**
     * @param id
     * @param name
     * @param roleDescriptor
     * @param job
     * @param resourceDescriptor
     */
    public Privilege(String id, String name, RoleDescriptor roleDescriptor, Job job, ResourceDescriptor resourceDescriptor) {
        setId(id);
        setName(name);
        setRoleDescriptor(roleDescriptor);
        setJob(job);
        setResourceDescriptor(resourceDescriptor);
    }

    public String toConstantName() {
        return new StringJoiner("_", "", "").add(job.name().toUpperCase()).add(name.toUpperCase()).toString();
    }

    private void setId(String id) {
        id = Objects.requireNonNull(id, "The id is required").trim();
        if (id.isEmpty() || id.length() > 64)
            throw new IllegalArgumentException("The id length is [1-64]");
        this.id = id;
    }

    private void setName(String name) {
        Objects.requireNonNull(name, "The name is required");
        if (name.isEmpty() || name.length() > 255)
            throw new IllegalArgumentException("The name must be 1 to 255 characters");
        this.name = name;
    }

    private void setResourceDescriptor(ResourceDescriptor resourceDescriptor) {
        Objects.requireNonNull(resourceDescriptor, "The resourceDescriptor required");
        this.resourceDescriptor = resourceDescriptor;
    }

    private void setJob(Job job) {
        Objects.requireNonNull(job, "The job required");
        this.job = job;
    }

    private void setRoleDescriptor(RoleDescriptor roleDescriptor) {
        Objects.requireNonNull(roleDescriptor, "The roleDescriptor required");
        this.roleDescriptor = roleDescriptor;
    }

    public String id() {
        return id;
    }

    public String name() {
        return name;
    }

    public ResourceDescriptor resourceDescriptor() {
        return resourceDescriptor;
    }

    public RoleDescriptor roleDescriptor() {
        return roleDescriptor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Privilege privilege = (Privilege) o;

        return id != null ? id.equals(privilege.id) : privilege.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Privilege.class.getSimpleName() + "[", "]")
                .add("id='" + id + "'")
                .add("name='" + name + "'")
                .add("resourceDescriptor=" + resourceDescriptor)
                .add("job=" + job)
                .add("roleDescriptor=" + roleDescriptor)
                .toString();
    }
}
