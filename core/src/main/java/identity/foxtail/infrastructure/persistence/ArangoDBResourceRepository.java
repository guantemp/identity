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
import com.arangodb.util.MapBuilder;
import com.arangodb.velocypack.VPackSlice;
import com.arangodb.velocypack.ValueType;
import identity.foxtail.core.domain.model.element.Resource;
import identity.foxtail.core.domain.model.element.ResourceRepository;
import identity.foxtail.core.domain.model.id.Creator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/***
 * @author <a href="www.foxtail.cc/authors/guan xianghuang">guan xiangHuan</a>
 * @since JDK8.0
 * @version 0.0.1 2018-01-31
 */
public class ArangoDBResourceRepository implements ResourceRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger(ArangoDBUserRepository.class);
    private static Field FIELD;
    private static Constructor<Creator> CONSTRUCTOR;

    static {
        try {
            CONSTRUCTOR = Creator.class.getDeclaredConstructor(String.class, String.class);
            CONSTRUCTOR.setAccessible(true);
            FIELD = Resource.class.getDeclaredField("treePath");
            FIELD.setAccessible(true);
        } catch (NoSuchFieldException | NoSuchMethodException e) {
            if (LOGGER.isDebugEnabled())
                LOGGER.debug("Not found treePath field or Creator constructor", e);
        }
    }

    private ArangoDatabase identity = ArangoDBUtil.getDatabase();

    @Override
    public Resource find(String id) {
        VPackSlice slice = identity.collection("resource").getDocument(id, VPackSlice.class);
        if (slice != null)
            return rebuild(slice);
        return null;
    }

    private Resource rebuild(VPackSlice slice) {
        try {
            String id = slice.get(DocumentField.Type.KEY.getSerializeName()).getAsString();
            String name = slice.get("name").getAsString();

            String userId = slice.get("creator").get("id").getAsString();
            String username = slice.get("creator").get("username").getAsString();
            Creator creator = CONSTRUCTOR.newInstance(userId, username);

            Resource resource = new Resource(id, name, creator);
            VPackSlice treePathSlice = slice.get("treePath");
            Deque<String> treePath = rebuildTreePath(treePathSlice);
            FIELD.set(resource, treePath);
            return resource;
        } catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
            if (LOGGER.isDebugEnabled())
                LOGGER.debug("Can't rebuild resource", e);
        }
        return null;
    }


    @Override
    public boolean hasChildren(String id) {
        final String query = "WITH resource,subordinate\n" +
                "FOR v,e IN 1..1 OUTBOUND @startVertex subordinate RETURN e";
        final Map<String, Object> bindVars = new MapBuilder().put("startVertex", "resource/" + id).get();
        ArangoCursor<VPackSlice> r = identity.query(query, bindVars, null, VPackSlice.class);
        return r.hasNext() ? true : false;
    }

    @Override
    public void save(Resource resource) {
        ArangoGraph graph = identity.graph("identity");
        boolean exists = identity.collection("resource").documentExists(resource.id());
        if (exists) {
            VertexUpdateEntity vertex = graph.vertexCollection("resource").updateVertex(resource.id(), resource);
            final String query = "WITH resource,subordinate\n" +
                    "FOR v,e IN 1..1 INBOUND @startVertex subordinate REMOVE e IN subordinate ";
            final Map<String, Object> bindVars = new MapBuilder().put("startVertex", "resource/" + resource.id()).get();
            identity.query(query, bindVars, null, VPackSlice.class);
            insertSubordinateEdge(graph, vertex, resource);
        } else {
            VertexEntity vertex = graph.vertexCollection("resource").insertVertex(resource);
            Creator creator = resource.creator();
            VertexEntity entity = graph.vertexCollection("user").getVertex(creator.id(), VertexEntity.class);
            if (entity != null)
                graph.edgeCollection("create").insertEdge(new Edge(entity.getId(), vertex.getId()));
            insertSubordinateEdge(graph, vertex, resource);
        }
    }

    /**
     * @param graph
     * @param vertex
     * @param resource
     */
    private void insertSubordinateEdge(ArangoGraph graph, DocumentEntity vertex, Resource resource) {
        String key = null;
        try {
            Deque<String> treePath = (Deque<String>) FIELD.get(resource);
            if (treePath != null)
                key = treePath.peekLast();
        } catch (IllegalAccessException e) {
            if (LOGGER.isDebugEnabled())
                LOGGER.debug("Can't power private filed treePath", e);
        }
        if (key != null) {
            VertexEntity entity = graph.vertexCollection("resource").getVertex(key, VertexEntity.class);
            if (entity != null)
                graph.edgeCollection("subordinate").insertEdge(new Edge(entity.getId(), vertex.getId()));
        }
    }

    @Override
    public Resource[] root() {
        final String query = "FOR v IN resource FILTER v.treePath == NULL RETURN v";
        ArangoCursor<VPackSlice> slices = identity.query(query, null, null, VPackSlice.class);
        List<Resource> resources = new ArrayList<>();
        while (slices.hasNext())
            resources.add(rebuild(slices.next()));
        return resources.toArray(new Resource[0]);
    }

    @Override
    public Resource[] children(String id) {
        final String query = "FOR v IN resource FILTER LAST(v.treePath) == @id RETURN v";
        final Map<String, Object> bindVars = new MapBuilder().put("id", id).get();
        ArangoCursor<VPackSlice> slices = identity.query(query, bindVars, null, VPackSlice.class);
        List<Resource> resources = new ArrayList<>();
        while (slices.hasNext())
            resources.add(rebuild(slices.next()));
        return resources.toArray(new Resource[0]);
    }

    @Override
    public void remove(String id) {
        if (!hasChildren(id))
            identity.graph("identity").vertexCollection("resource").deleteVertex(id);
    }

    @Override
    public Resource parent(String id) {
        VPackSlice slice = identity.collection("resource").getDocument(id, VPackSlice.class);
        VPackSlice treePathSlice = slice.get("treePath");
        Deque<String> treePath = rebuildTreePath(treePathSlice);
        String parentId = treePath.peekLast();
        return parentId == null ? null : find(parentId);
    }

    private Deque<String> rebuildTreePath(VPackSlice treePathSlice) {
        if (treePathSlice.getType() == ValueType.ARRAY) {
            Iterator<VPackSlice> iterator = treePathSlice.arrayIterator();
            if (iterator.hasNext()) {
                Deque<String> treePath = new ArrayDeque<>();
                while (iterator.hasNext()) {
                    treePath.offerLast(iterator.next().getAsString());
                }
                return treePath;
            }
        }
        return new ArrayDeque<>(0);
    }

    private static class Edge {
        @DocumentField(DocumentField.Type.FROM)
        private String from;

        @DocumentField(DocumentField.Type.TO)
        private String to;

        public Edge(String from, String to) {
            this.from = from;
            this.to = to;
        }
    }
}
