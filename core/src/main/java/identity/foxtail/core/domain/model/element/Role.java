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

import com.arangodb.entity.DocumentField;
import com.arangodb.velocypack.annotations.Expose;
import identity.foxtail.core.domain.model.DomainRegistry;
import identity.foxtail.core.domain.model.id.Group;
import identity.foxtail.core.domain.model.id.GroupMemberService;
import identity.foxtail.core.domain.model.id.User;
import mi.foxtail.id.ObjectId;

import java.util.Objects;
import java.util.StringJoiner;

/***
 * @author <a href="www.foxtail.cc/authors/guan xianghuang">guan xiangHuan</a>
 * @since JDK8.0
 * @version 0.0.3 2018-11-19
 */
public class Role {
    @DocumentField(DocumentField.Type.KEY)
    private String id;
    private String name;
    private String description;
    @Expose(serialize = false, deserialize = false)
    private Group group;

    /**
     * @param id
     * @param name
     */
    public Role(String id, String name) {
        this(id, name, null);
    }

    /**
     * @param id
     * @param name
     * @param description
     * @throws IllegalArgumentException if name is <code>NULL</code> or empty
     *                                  The name is <code>NULL</code>
     */
    public Role(String id, String name, String description) {
        setId(id);
        createInternalGroup();
        setName(name);
        this.description = description;
    }

/*
    public Set<Permission> permissions() {
        return Collections.unmodifiableSet(permissions);
    }
*/

    /**
     * @return
     */
    public Group group() {
        return group;
    }

    /**
     * @param group
     * @param service
     */
    public void assignGroup(Group group, GroupMemberService service) {
        Objects.requireNonNull(service, "Group service is required");
        Objects.requireNonNull(group, "Group is required");
        if (this.group.addGroup(group, service))
            DomainRegistry.domainEventPublisher().publish(new GroupAssignedToRole(id, group.id()));
    }

    /**
     * @param user
     */
    public void assignUser(User user) {
        Objects.requireNonNull(user, "User is required");
        if (group.addUser(user))
            DomainRegistry.domainEventPublisher().publish(new UserAssignedToRole(id, user.id()));
    }

    /**
     * @param group
     */
    public void unassignGroup(Group group) {
        Objects.requireNonNull(group, "Group is required");
        if (this.group.removeGroup(group))
            DomainRegistry.domainEventPublisher().publish(new GroupUnassignedFromRole(id, group.id()));
    }

    /**
     * @param user
     */
    public void unassignUser(User user) {
        Objects.requireNonNull(user, "User is required");
        if (group.removeUser(user))
            DomainRegistry.domainEventPublisher().publish(new UserUnassignedFromRole(id, user.id()));
    }

    /**
     * @param user
     * @return
     */
    public boolean isUserInRole(User user, GroupMemberService service) {
        return group.isUserInGroup(user, service);
    }

    /**
     * @return the role id
     */
    public String id() {
        return id;
    }

    /**
     * @return the name
     */
    public String name() {
        return name;
    }

    /**
     * @param name
     * @throws IllegalArgumentException if name no changed
     */
    public void rename(String name) {
        if (!this.name.equals(name)) {
            setName(name);
            DomainRegistry.domainEventPublisher().publish(new RoleRenamed(this.id, name));
        }
    }

    /**
     * @return the description
     */
    public String description() {
        return description;
    }

    /**
     * @param description
     */
    public void changDescription(String description) {
        if (this.description != null && !this.description.equals(description)) {
            this.description = description;
            DomainRegistry.domainEventPublisher().publish(new RoleDescriptionChanged(id, description));
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Role role = (Role) o;

        return id != null ? id.equals(role.id) : role.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    public RoleDescriptor toRoleDescriptor() {
        return new RoleDescriptor(id, name);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Role.class.getSimpleName() + "[", "]")
                .add("id='" + id + "'")
                .add("name='" + name + "'")
                .add("description='" + description + "'")
                .add("group=" + group)
                .toString();
    }

    protected void setId(String id) {
        id = Objects.requireNonNull(id, "Name is required").trim();
        if (id.isEmpty() || id.length() > 64)
            throw new IllegalArgumentException("The name must be 1-64 characters");
        this.id = id;
    }

    /**
     * @param name
     */
    protected void setName(String name) {
        name = Objects.requireNonNull(name, "name is required");
        if (name.isEmpty() || name.length() > 128)
            throw new IllegalArgumentException("Name must be 128 characters or less");
        this.name = name;
    }

    protected void createInternalGroup() {
        final String groupName = Group.ROLE_GROUP_PREFIX + new ObjectId().id();
        group = new Group(groupName, groupName, "this is a internal group");
    }
}
