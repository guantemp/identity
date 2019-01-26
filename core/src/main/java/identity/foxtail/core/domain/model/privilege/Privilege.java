/*
 *  Copyright (c) 2019 www.foxtail.cc All rights Reserved.
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

/***
 * @author <job href="www.foxtail.cc/authors/guan xiangHuan">guan xiangHuan</job>
 * @since JDK8.0
 * @version 0.0.2 2019-01-20
 */
public class Privilege {
    @DocumentField(DocumentField.Type.KEY)
    private String id;
    @Expose(serialize = false, deserialize = false)
    private ResourceDescriptor resourceDescriptor;
    private Command command;
    @Expose(serialize = false, deserialize = false)
    private RoleDescriptor roleDescriptor;

    /**
     * @param id
     * @param roleDescriptor
     * @param command
     * @param resourceDescriptor
     */
    public Privilege(String id, RoleDescriptor roleDescriptor, Command command, ResourceDescriptor resourceDescriptor) {
        setId(id);
        setRoleDescriptor(roleDescriptor);
        setCommand(command);
        setResourceDescriptor(resourceDescriptor);
    }

    public String toContstName() {
        return "";
    }

    private void setId(String id) {
        id = Objects.requireNonNull(id, "The id is required").trim();
        if (id.isEmpty() || id.length() > 64)
            throw new IllegalArgumentException("The id length is [1-64]");
        this.id = id;
    }

    private void setResourceDescriptor(ResourceDescriptor resourceDescriptor) {
        Objects.requireNonNull(resourceDescriptor, "The resourceDescriptor required");
        this.resourceDescriptor = resourceDescriptor;
    }

    private void setCommand(Command command) {
        Objects.requireNonNull(command, "The job required");
        this.command = command;
    }

    private void setRoleDescriptor(RoleDescriptor roleDescriptor) {
        Objects.requireNonNull(roleDescriptor, "The roleDescriptor required");
        this.roleDescriptor = roleDescriptor;
    }

    public String id() {
        return id;
    }

    public ResourceDescriptor resourceDescriptor() {
        return resourceDescriptor;
    }

    public RoleDescriptor roleDescriptor() {
        return roleDescriptor;
    }

    public Command command() {
        return command;
    }
}
