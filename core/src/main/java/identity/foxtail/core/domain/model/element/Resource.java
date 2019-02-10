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
import identity.foxtail.core.domain.model.DomainRegistry;
import identity.foxtail.core.domain.model.id.Creator;
import identity.foxtail.core.domain.model.id.User;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;

/***
 * @author <a href="www.foxtail.cc/authors/guan xianghuang">guan xiangHuan</a>
 * @since JDK8.0
 * @version 0.0.1 2018-05-10
 */

public class Resource {
    private static final int DEPTH = 32;
    @DocumentField(DocumentField.Type.KEY)
    private String id;
    private String name;
    private Creator creator;
    private Deque<String> treePath;

    /**
     * @param id
     * @param name
     * @param creator
     * @throws IllegalArgumentException if id is null or length is greater  than 24 characters
     *                                  if creator is null
     *                                  if name is null or length range not in [1-128]
     */
    public Resource(String id, String name, Creator creator) {
        setId(id);
        setName(name);
        setCreator(creator);
    }

    public Creator creator() {
        return creator;
    }

    private void setName(String name) {
        Objects.requireNonNull(name, "Name is required");
        if (name.isEmpty() || name.length() > 128)
            throw new IllegalArgumentException("The name length must be less than 128 characters");
        this.name = name;
    }

    private void setCreator(Creator creator) {
        this.creator = Objects.requireNonNull(creator, "creator is required");
    }

    public String id() {
        return id;
    }

    /**
     * @param user
     * @return
     */
    public boolean isCreator(User user) {
        return user.id().equals(creator.id());
    }

    public String name() {
        return name;
    }

    /**
     * @param name
     */
    public void rename(String name) {
        if (this.name.equals(name))
            throw new IllegalArgumentException("Name not changed");
        setName(name);
        DomainRegistry.domainEventPublisher().publish(new ResourceRenamed(id, name));
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Resource{");
        sb.append("id='").append(id).append('\'');
        sb.append(", name='").append(name).append('\'');
        sb.append(", creator=").append(creator);
        sb.append(", treePath=").append(treePath);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Resource resource = (Resource) o;

        return id != null ? id.equals(resource.id) : resource.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    private void setId(String id) {
        Objects.requireNonNull(id, "id is required");
        if (id.isEmpty() || id.length() > 64)
            throw new IllegalArgumentException("id length must be less than 64 characters");
        this.id = id;
    }

    /**
     * @return
     */
    public ResourceDescriptor toResourceDescriptor() {
        return new ResourceDescriptor(id, name, creator);
    }

    /**
     * support depth is 32
     *
     * @param parent
     */
    public void assignTo(Resource parent) {
        if (parent != null) {
            treePath = new ArrayDeque<>();
            if (parent.treePath != null && parent.treePath.size() < DEPTH) {
                for (String s : parent.treePath) {
                    treePath.offerLast(s);
                }
            }
            treePath.offerLast(parent.id);
            DomainRegistry.domainEventPublisher().publish(new ResourceAssignedToResource(id, parent.id));
        }
    }

    /**
     * Will set treePath is <code>NULL</code>
     */
    public void unassign() {
        treePath = null;
        DomainRegistry.domainEventPublisher().publish(new ResourceUnassigned(id));
    }
}
