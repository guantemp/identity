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
package identity.foxtail.infrastructure.persistence;

import com.arangodb.ArangoCursor;
import com.arangodb.ArangoDatabase;
import com.arangodb.ArangoGraph;
import com.arangodb.entity.DocumentEntity;
import com.arangodb.entity.DocumentField;
import com.arangodb.entity.VertexEntity;
import com.arangodb.entity.VertexUpdateEntity;
import com.arangodb.model.VertexUpdateOptions;
import com.arangodb.util.MapBuilder;
import com.arangodb.velocypack.VPackSlice;
import identity.foxtail.core.domain.model.element.Role;
import identity.foxtail.core.domain.model.element.RoleRepository;
import identity.foxtail.core.domain.model.id.Group;
import identity.foxtail.core.domain.model.id.GroupMember;
import identity.foxtail.core.domain.model.id.GroupMemberType;
import mi.foxtail.id.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/***
 * @author <a href="www.foxtail.cc/author-/guan xiangHuan">guan xiangHuan</a>
 * @since JDK8.0
 * @version 0.0.1 2018-01-22
 */
public class ArangoDBRoleRepository implements RoleRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger(ArangoDBRoleRepository.class);
    private static final VertexUpdateOptions UPDATE_OPTIONS = new VertexUpdateOptions().keepNull(false);
    private static Field membersField;
    private static Constructor<GroupMember> groupMemberConstructor;

    static {
        try {
            groupMemberConstructor = GroupMember.class.getDeclaredConstructor(GroupMemberType.class, String.class);
            groupMemberConstructor.setAccessible(true);
            membersField = Group.class.getDeclaredField("members");
            membersField.setAccessible(true);
        } catch (NoSuchFieldException | NoSuchMethodException e) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Not find such field or constructor", e);
            }
        }
    }

    private ArangoDatabase identity = ArangoDBUtil.getDatabase();

    @Override
    public Role[] all(int offset, int limit) {
        Role[] roles = ArangoDBUtil.calculationCollectionSize(identity, Role.class, offset, limit);
        final String query = "FOR v IN role LIMIT @offset,@limit RETURN v";
        final Map<String, Object> bindVars = new MapBuilder().put("offset", offset).put("limit", limit).get();
        final ArangoCursor<VPackSlice> slices = identity.query(query, bindVars, null, VPackSlice.class);
        try {
            for (int i = 0; slices.hasNext(); i++)
                roles[i] = (rebuild(identity, slices.next()));
        } catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Group membersField create fail", e);
            }
        }
        return roles;
    }

    @Override
    public int count() {
        int count = 0;
        final String countQuery = " RETURN LENGTH(role)";
        final ArangoCursor<VPackSlice> cursor = identity.query(countQuery, null, null, VPackSlice.class);
        for (; cursor.hasNext(); ) {
            count = cursor.next().getAsInt();
        }
        return count;
    }

    @Override
    public void remove(String id) {
        ArangoGraph graph = identity.graph("identity");
        graph.vertexCollection("role").deleteVertex(id);
    }

    @Override
    public String nextIdentity() {
        return new ObjectId().id();
    }

    @Override
    public Role find(String id) {
        VPackSlice slice = identity.collection("role").getDocument(id, VPackSlice.class);
        if (slice != null) {
            try {
                return rebuild(identity, slice);
            } catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Role create fail", e);
                }
            }
        }
        return null;
    }

    private Role rebuild(ArangoDatabase db, VPackSlice slice) throws IllegalAccessException,
            InvocationTargetException, InstantiationException {
        String roleId = slice.get(DocumentField.Type.KEY.getSerializeName()).getAsString();
        String name = slice.get("name").getAsString();
        String description = null;
        if (slice.get("description").isNull() || slice.get("description").isNone())
            description = slice.get("description").getAsString();
        Role role = new Role(roleId, name, description);
        //rebuild group member
        Set<GroupMember> members = new HashSet<>();
        final String groupQuery = "WITH role,act " +
                "FOR v,e IN 1..1 INBOUND @startVertex act FILTER e.groupMemberType == @groupMemberType RETURN v";
        final Map<String, Object> gBindVars = new MapBuilder().put("startVertex", "role/" + roleId).put("groupMemberType", "GROUP").get();
        ArangoCursor<VPackSlice> g = db.query(groupQuery, gBindVars, null, VPackSlice.class);
        while (g.hasNext()) {
            VPackSlice v = g.next();
            members.add(groupMemberConstructor.newInstance(GroupMemberType.GROUP, v.get(DocumentField.Type.KEY.getSerializeName()).getAsString()));
        }
        final Map<String, Object> uBindVars = new MapBuilder().put("startVertex", "role/" + roleId).put("groupMemberType", "USER").get();
        ArangoCursor<VPackSlice> u = db.query(groupQuery, uBindVars, null, VPackSlice.class);
        while (u.hasNext()) {
            VPackSlice v = u.next();
            members.add(groupMemberConstructor.newInstance(GroupMemberType.USER, v.get(DocumentField.Type.KEY.getSerializeName()).getAsString()));
        }
        membersField.set(role.group(), members);
        /*
        final String permissionQuery = "WITH role,action" +
                "    FOR v,e IN 1..1 OUTBOUND @startVertex action RETURN {\"key\": v._key, \"name\": v.name ,\"action\":e.action}";
        final Map<String, Object> pBindVars = new MapBuilder().put("startVertex", "role/" + roleId).get();
        ArangoCursor<VPackSlice> permissionCursor = db.query(permissionQuery, pBindVars, null, VPackSlice.class);
        Set<Permission> permissionSet = new HashSet<>();
        while (permissionCursor.hasNext()) {
            VPackSlice pSlice = permissionCursor.next();
            ResourceDescriptor resourceDescriptor = resourceDescriptorConstructor.newInstance(pSlice.get("key").getAsString(), pSlice.get("name").getAsString());
            Permission element = permissionConstructor.newInstance(resourceDescriptor, PreinstallAction.valueOf(pSlice.get("action").getAsString()));
            permissionSet.add(element);
        }
        permissions.set(role, permissionSet);
        */
        return role;
    }

    @Override
    public void save(Role role) {
        ArangoGraph graph = identity.graph("identity");
        boolean exists = identity.collection("role").documentExists(role.id());
        if (exists) {
            VertexUpdateEntity vertex = graph.vertexCollection("role").updateVertex(role.id(), role, UPDATE_OPTIONS);
            /*
            StringBuilder sb = new StringBuilder("WITH role,act\n" +
                    "FOR v,e IN 1..1 OUTBOUND '").append(vertex.getId()).append("' has FILTER v._id NOT IN[");
            for (GroupMember member : role.group().members()) {
                if (member.isGroup())
                    sb.append("'group/").append(member.userOrGroupId()).append("',");
                else
                    sb.append("'user/").append(member.userOrGroupId()).append("',");
            }
            sb.deleteCharAt(sb.length() - 1);
            sb.append("] REMOVE e IN act");
            System.out.println(sb);
            */
            final String query = "WITH role,act " +
                    "FOR v,e IN 1..1 INBOUND @startVertex act REMOVE e IN act";
            final Map<String, Object> bindVars = new MapBuilder().put("startVertex", "role/" + role.id()).get();
            identity.query(query, bindVars, null, VPackSlice.class);
            insertActorEdge(graph, vertex, role.group().members());
        } else {
            VertexEntity vertex = graph.vertexCollection("role").insertVertex(role);
            insertActorEdge(graph, vertex, role.group().members());
            //insertActionEdge(role.permissions(), graph, vertex);
        }
    }


    /**
     * @param members
     * @param graph
     * @param vertex
     */
    private void insertActorEdge(ArangoGraph graph, DocumentEntity vertex, Collection<GroupMember> members) {
        for (GroupMember member : members) {
            if (member.isUser()) {
                VertexEntity uVertex = graph.vertexCollection("user").getVertex(member.userOrGroupId(), VertexEntity.class);
                if (uVertex != null)
                    graph.edgeCollection("act").insertEdge(new ActEdge(uVertex.getId(), vertex.getId(), GroupMemberType.USER));
            }
            if (member.isGroup()) {
                VertexEntity gVertex = graph.vertexCollection("group").getVertex(member.userOrGroupId(), VertexEntity.class);
                if (gVertex != null)
                    graph.edgeCollection("act").insertEdge(new ActEdge(gVertex.getId(), vertex.getId(), GroupMemberType.GROUP));
            }
        }
    }

    private static class ActEdge {
        @DocumentField(DocumentField.Type.FROM)
        private String from;

        @DocumentField(DocumentField.Type.TO)
        private String to;
        private GroupMemberType groupMemberType;

        public ActEdge(String from, String to, GroupMemberType groupMemberType) {
            this.from = from;
            this.to = to;
            this.groupMemberType = groupMemberType;
        }
    }
}
