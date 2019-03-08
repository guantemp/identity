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

import com.arangodb.entity.DocumentField;
import com.arangodb.velocypack.annotations.Expose;
import identity.foxtail.core.domain.model.element.ResourceDescriptor;
import identity.foxtail.core.domain.model.element.RoleDescriptor;

import java.util.Objects;

/***
 * @author <job href="www.foxtail.cc/authors/guan xiangHuan">guan xiangHuan</job>
 * @since JDK8.0
 * @version 0.0.2 2019-01-30
 */
public class Permission {
    @DocumentField(DocumentField.Type.KEY)
    private String id;
    private String name;
    @Expose(serialize = false, deserialize = false)
    private ResourceDescriptor resourceDescriptor;
    private Processor processor;
    private Schedule schedule;
    @Expose(serialize = false, deserialize = false)
    private RoleDescriptor roleDescriptor;

    public Permission(String id, String name, RoleDescriptor roleDescriptor, Processor processor, ResourceDescriptor resourceDescriptor) {
        this(id, name, roleDescriptor, null, processor, resourceDescriptor);
    }

    public Permission(String id, String name, RoleDescriptor roleDescriptor, Schedule schedule, Processor processor, ResourceDescriptor resourceDescriptor) {
        setId(id);
        setName(name);
        setRoleDescriptor(roleDescriptor);
        this.schedule = schedule;
        setProcessor(processor);
        setResourceDescriptor(resourceDescriptor);
    }

    public Schedule schedule() {
        return schedule;
    }

    private void setName(String name) {
        name = Objects.requireNonNull(name, "name is required").trim();
        this.name = name;
    }

    public String name() {
        return name;
    }

    public boolean isInSchedule() {
        return schedule == null ? true : schedule.isInSchedule();
    }

    public Result execute(VariantContext vc) {
        return processor.engine().execute(vc);
    }

    private void setResourceDescriptor(ResourceDescriptor resourceDescriptor) {
        Objects.requireNonNull(resourceDescriptor, "The resourceDescriptor required");
        this.resourceDescriptor = resourceDescriptor;
    }

    private void setProcessor(Processor processor) {
        Objects.requireNonNull(processor, "The job required");
        this.processor = processor;
    }

    private void setRoleDescriptor(RoleDescriptor roleDescriptor) {
        Objects.requireNonNull(roleDescriptor, "The roleDescriptor required");
        this.roleDescriptor = roleDescriptor;
    }

    public ResourceDescriptor resourceDescriptor() {
        return resourceDescriptor;
    }

    public RoleDescriptor roleDescriptor() {
        return roleDescriptor;
    }

    public Processor processor() {
        return processor;
    }

    public String id() {
        return id;
    }

    private void setId(String id) {
        id = Objects.requireNonNull(id, "id required").trim();
        if (id.isEmpty() || id.length() >= 128)
            throw new IllegalArgumentException("id length is 1-128");
        this.id = id;
    }

    @Override
    public String toString() {
        return "Permission{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", resourceDescriptor=" + resourceDescriptor +
                ", processor=" + processor +
                ", schedule=" + schedule +
                ", roleDescriptor=" + roleDescriptor +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Permission that = (Permission) o;

        return id != null ? id.equals(that.id) : that.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
