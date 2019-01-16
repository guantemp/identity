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

package identity.foxtail.domain.model.power;

import com.arangodb.entity.DocumentField;
import com.arangodb.velocypack.annotations.Expose;
import identity.foxtail.core.domain.model.element.ResourceDescriptor;
import identity.foxtail.core.domain.model.element.RoleDescriptor;
import identity.foxtail.core.domain.model.job.Command;
import identity.foxtail.core.domain.model.job.Schedule;

import java.util.Objects;
import java.util.StringJoiner;

/***
 * @author <a href="www.foxtail.cc/authors/guan xiangHuan">guan xiangHuan</a>
 * @since JDK8.0
 * @version 0.0.1 2018-12-03
 */
public class Power {
    @DocumentField(DocumentField.Type.KEY)
    private String id;
    private String name;
    @Expose(serialize = false, deserialize = false)
    private ResourceDescriptor resourceDescriptor;
    private Command command;
    private Schedule schedule;
    @Expose(serialize = false, deserialize = false)
    private RoleDescriptor roleDescriptor;

    /**
     * @param id
     * @param name
     * @param roleDescriptor
     * @param command
     * @param resourceDescriptor
     * @param schedule
     */
    public Power(String id, String name, RoleDescriptor roleDescriptor, Command command, ResourceDescriptor resourceDescriptor, Schedule schedule) {
        setId(id);
        setName(name);
        setRoleDescriptor(roleDescriptor);
        setCommand(command);
        setResourceDescriptor(resourceDescriptor);
        this.schedule = schedule;
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

    private void setCommand(Command command) {
        Objects.requireNonNull(command, "The command required");
        this.command = command;
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

    public Command command() {
        return command;
    }

    public Schedule schedule() {
        return schedule;
    }

    public RoleDescriptor roleDescriptor() {
        return roleDescriptor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Power power = (Power) o;

        return id != null ? id.equals(power.id) : power.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Power.class.getSimpleName() + "[", "]")
                .add("id='" + id + "'")
                .add("name='" + name + "'")
                .add("resourceDescriptor=" + resourceDescriptor)
                .add("command=" + command)
                .add("schedule=" + schedule)
                .add("roleDescriptor=" + roleDescriptor)
                .toString();
    }
}
