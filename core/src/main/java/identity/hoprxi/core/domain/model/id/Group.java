/*
 *  Copyright 2018 www.hoprxi.com All rights Reserved.
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
package identity.hoprxi.core.domain.model.id;

import com.arangodb.entity.DocumentField;
import identity.hoprxi.core.domain.model.DomainRegistry;

import java.util.*;

/***
 * @author <a href="www.hoprxi.com/authors/guan xiangHuan">guan xiangHuan</a>
 * @since JDK8.0
 * @version 0.0.2 2018-01-09
 */
public class Group {
    public static final String ROLE_GROUP_PREFIX = "ROLE-INTERNAL-GROUP: ";
    private String description;
    @DocumentField(DocumentField.Type.KEY)
    private String id;
    private Set<GroupMember> members;
    private String name;

    /**
     * @param id
     * @param name
     * @param description
     * @throws IllegalArgumentException if name less than and equal zero or name is <code>NULL</code>
     */
    public Group(String id, String name, String description) {
        setId(id);
        setName(name);
        this.members = new HashSet<>();
        this.description = description;
    }

    /**
     * @param id
     * @param name
     * @throws IllegalArgumentException if name less than and equal zero or name is <code>NULL</code>
     */
    public Group(String id, String name) {
        this(id, name, null);
    }

    /**
     * Add a saved group to the current group
     *
     * @param group
     */
    public boolean addGroup(Group group, GroupMemberService service) {
        Objects.requireNonNull(group, "group required");
        GroupMember member = group.toGroupMember();
        if (!service.isMemberInGroup(this, member)) {
            if (members.add(member)) {
                DomainRegistry.domainEventPublisher().publish(new GroupGroupAdded(id, group.id()));
                return true;
            }
        }
        return false;
    }

    /**
     * Add a saved user to the current group
     *
     * @param user
     */
    public boolean addUser(User user) {
        Objects.requireNonNull(user, "user required");
        if (members.add(user.toGroupMember())) {
            DomainRegistry.domainEventPublisher().publish(new GroupUserAdded(id, user.id()));
            return true;
        }
        return false;
    }

    public String description() {
        return description;
    }

    /**
     * @param description
     */
    public void changDescription(String description) {
        if (this.description != null && !this.description.equals(description)) {
            this.description = description;
            DomainRegistry.domainEventPublisher().publish(new GroupDescriptionChanged(id, description));
        }
    }

    public String id() {
        return id;
    }

    public Set<GroupMember> members() {
        return Collections.unmodifiableSet(members);
    }

    public String name() {
        return name;
    }

    /**
     * @param name
     */
    public void rename(String name) {
        if (!this.name.equals(name)) {
            setName(name);
            DomainRegistry.domainEventPublisher().publish(new GroupRenamed(id, name));
        }
    }

    /**
     * not a nested remove, only direct member
     *
     * @param group
     */
    public boolean removeGroup(Group group) {
        if (members.remove(group.toGroupMember())) {
            DomainRegistry.domainEventPublisher().publish(new GroupGroupRemoved(id, group.id));
            return true;
        }
        return false;
    }

    /**
     * not a nested remove, only direct member
     *
     * @param user
     */
    public boolean removeUser(User user) {
        if (members.remove(user.toGroupMember())) {
            DomainRegistry.domainEventPublisher().publish(new GroupUserRemoved(id, user.id()));
            return true;
        }
        return false;
    }

    /**
     * @param user
     * @param service
     * @return true if user is in group or nested group
     */
    public boolean isUserInGroup(User user, GroupMemberService service) {
        return service.isMemberInGroup(this, user.toGroupMember());
    }

    protected GroupMember toGroupMember() {
        return new GroupMember(GroupMemberType.GROUP, id);
    }

    private void setName(String name) {
        Objects.requireNonNull(name, "The name is required");
        if (name.length() < 1 || name.length() > 255)
            throw new IllegalArgumentException("The name must be 1 to 255 characters");
        this.name = name;
    }

    private void setId(String id) {
        id = Objects.requireNonNull(id, "The id is required").trim();
        if (id.isEmpty() || id.length() > 128)
            throw new IllegalArgumentException("id length must be 1 to 128 characters");
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Group group = (Group) o;

        return id != null ? id.equals(group.id) : group.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Group.class.getSimpleName() + "[", "]")
                .add("description='" + description + "'")
                .add("id='" + id + "'")
                .add("members=" + members)
                .add("name='" + name + "'")
                .toString();
    }
}
