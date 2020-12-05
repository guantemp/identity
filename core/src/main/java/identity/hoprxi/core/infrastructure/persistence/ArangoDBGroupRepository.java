/*
 * Copyright (c) 2020 www.hoprxi.com All Rights Reserved.
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
package identity.hoprxi.core.infrastructure.persistence;

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
import identity.hoprxi.core.domain.model.id.Group;
import identity.hoprxi.core.domain.model.id.GroupMember;
import identity.hoprxi.core.domain.model.id.GroupMemberType;
import identity.hoprxi.core.domain.model.id.GroupRepository;
import mi.hoprxi.id.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/***
 * @author <a href="www.hoprxi.com/authors/guan xiangHuan">guan xiangHuan</a>
 * @since JDK8.0
 * @version 0.0.1 2018-01-15
 */
public class ArangoDBGroupRepository implements GroupRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger(ArangoDBGroupRepository.class);
    private static final VertexUpdateOptions UPDATE_OPTIONS = new VertexUpdateOptions().keepNull(false);
    private static Constructor<GroupMember> GROUPMEMBER_CONSTRUCTOR;
    private static Field MEMBERS_FIELD;

    static {
        try {
            GROUPMEMBER_CONSTRUCTOR = GroupMember.class.getDeclaredConstructor(GroupMemberType.class, String.class);
            GROUPMEMBER_CONSTRUCTOR.setAccessible(true);
            MEMBERS_FIELD = Group.class.getDeclaredField("members");
            MEMBERS_FIELD.setAccessible(true);
        } catch (NoSuchMethodException | NoSuchFieldException e) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Not find matching constructor or field", e);
            }
        }
    }

    private ArangoDatabase identity = ArangoDBUtil.getResource().db("identity");

    @Override
    public Group[] all(long offset, int limit) {
        Group groups[] = ArangoDBUtil.calculationCollectionSize(identity, Group.class, offset, limit);
        final String query = "FOR v IN group LIMIT @offset,@limit RETURN v";
        final Map<String, Object> bindVars = new MapBuilder().put("offset", offset).put("limit", limit).get();
        final ArangoCursor<VPackSlice> slices = identity.query(query, bindVars, null, VPackSlice.class);
        try {
            for (int i = 0; slices.hasNext(); i++)
                groups[i] = rebuild(identity, slices.next());
        } catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Group create fail", e);
            }
        }
        return groups;
    }

    @Override
    public int count() {
        int count = 0;
        final String countQuery = " RETURN LENGTH(group)";
        final ArangoCursor<VPackSlice> cursor = identity.query(countQuery, null, null, VPackSlice.class);
        for (; cursor.hasNext(); ) {
            count = cursor.next().getAsInt();
        }
        return count;
    }


    @Override
    public Group find(String id) {
        VPackSlice slice = identity.collection("group").getDocument(id, VPackSlice.class);
        if (slice != null) {
            try {
                return rebuild(identity, slice);
            } catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Group create fail", e);
                }
            }
        }
        return null;
    }

    @Override
    public String nextIdentity() {
        return new ObjectId().id();
    }

    @Override
    public void remove(String id) {
        final String query = "WITH group FOR v,e IN 1..1 OUTBOUND @startVertex subordinate RETURN v";
        final Map<String, Object> bindVars = new MapBuilder().put("startVertex", "group/" + id).get();
        ArangoCursor<VPackSlice> parent = identity.query(query, bindVars, null, VPackSlice.class);
        if (!parent.hasNext())
            identity.graph("identity").vertexCollection("group").deleteVertex(id);
    }

    @Override
    public void save(Group group) {
        ArangoGraph graph = identity.graph("identity");
        boolean exists = identity.collection("group").documentExists(group.id());
        if (exists) {
            final String query = "WITH group,subordinate FOR v,e IN 1..1 OUTBOUND @startVertex subordinate REMOVE e IN subordinate";
            final Map<String, Object> bindVars = new MapBuilder().put("startVertex", "group/" + group.id()).get();
            identity.query(query, bindVars, null, VPackSlice.class);
            VertexUpdateEntity vertex = graph.vertexCollection("group").updateVertex(group.id(), group, UPDATE_OPTIONS);
            insertContainEdge(graph, group, vertex);
        } else {
            VertexEntity vertex = graph.vertexCollection("group").insertVertex(group);
            insertContainEdge(graph, group, vertex);
        }
    }

    private void insertContainEdge(ArangoGraph graph, Group group, DocumentEntity vertex) {
        for (GroupMember member : group.members()) {
            if (member.isUser()) {
                VertexEntity uVertex = graph.vertexCollection("user").getVertex(member.userOrGroupId(), VertexEntity.class);
                if (uVertex != null)
                    graph.edgeCollection("subordinate").insertEdge(new Subordinate(vertex.getId(), uVertex.getId(), GroupMemberType.USER));
            } else if (member.isGroup()) {
                VertexEntity g2Vertex = graph.vertexCollection("group").getVertex(member.userOrGroupId(), VertexEntity.class);
                if (g2Vertex != null)
                    graph.edgeCollection("subordinate").insertEdge(new Subordinate(vertex.getId(), g2Vertex.getId(), GroupMemberType.GROUP));
            }
        }
    }

    private Group rebuild(ArangoDatabase db, VPackSlice slice) throws IllegalAccessException, InvocationTargetException,
            InstantiationException {
        if (slice == null)
            return null;
        String groupId = slice.get(DocumentField.Type.KEY.getSerializeName()).getAsString();
        String name = slice.get("name").getAsString();
        String description = null;
        if (slice.get("description").isNull())
            description = slice.get("description").getAsString();
        Group group = new Group(groupId, name, description);
        Set<GroupMember> members = new HashSet<>();
/*
        final String query = "WITH group,contain FOR v,e IN 1..1 OUTBOUND @startVertex contain RETURN v";
        Constructor<GroupMember> GROUPMEMBER_CONSTRUCTOR = GroupMember.class.getDeclaredConstructor(GroupMemberType.class, String.class);
        GROUPMEMBER_CONSTRUCTOR.setAccessible(true);
        final Map<String, Object> gBindVars = new MapBuilder().put("startVertex", "group/" + id).get();
        ArangoCursor<VPackSlice> g = db.query(query, gBindVars, null, VPackSlice.class);
        for (; g.hasNext(); ) {
            VPackSlice v = g.next();
            if (v.get(DocumentField.Type.ID.getSerializeName()).getAsString().startsWith("group")) {
                members.add(GROUPMEMBER_CONSTRUCTOR.newInstance(GroupMemberType.GROUP, v.get(DocumentField.Type.KEY.getSerializeName()).getAsString()));
            } else {
                members.add(GROUPMEMBER_CONSTRUCTOR.newInstance(GroupMemberType.USER, v.get(DocumentField.Type.KEY.getSerializeName()).getAsString()));
            }
        }
        */
        final String query = "WITH group,subordinate FOR v,e IN 1..1 OUTBOUND @startVertex subordinate FILTER e.type==@type RETURN v";
        final Map<String, Object> gBindVars = new MapBuilder().put("startVertex", "group/" + groupId).put("type", "GROUP").get();
        ArangoCursor<VPackSlice> g = db.query(query, gBindVars, null, VPackSlice.class);
        while (g.hasNext()) {
            VPackSlice v = g.next();
            members.add(GROUPMEMBER_CONSTRUCTOR.newInstance(GroupMemberType.GROUP, v.get(DocumentField.Type.KEY.getSerializeName()).getAsString()));
        }
        final Map<String, Object> uBindVars = new MapBuilder().put("startVertex", "group/" + groupId).put("type", "USER").get();
        ArangoCursor<VPackSlice> u = db.query(query, uBindVars, null, VPackSlice.class);
        while (u.hasNext()) {
            VPackSlice v = u.next();
            members.add(GROUPMEMBER_CONSTRUCTOR.newInstance(GroupMemberType.USER, v.get(DocumentField.Type.KEY.getSerializeName()).getAsString()));
        }
        MEMBERS_FIELD.set(group, members);
        return group;
    }

    private static class Subordinate {
        @DocumentField(DocumentField.Type.FROM)
        private String from;

        @DocumentField(DocumentField.Type.TO)
        private String to;
        private GroupMemberType type;

        public Subordinate(String from, String to, GroupMemberType type) {
            this.from = from;
            this.to = to;
            this.type = type;
        }
    }
}
