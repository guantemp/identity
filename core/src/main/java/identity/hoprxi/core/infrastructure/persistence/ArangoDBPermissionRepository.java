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
import com.arangodb.ArangoEdgeCollection;
import com.arangodb.ArangoGraph;
import com.arangodb.entity.DocumentField;
import com.arangodb.entity.VertexEntity;
import com.arangodb.model.EdgeUpdateOptions;
import com.arangodb.util.MapBuilder;
import com.arangodb.velocypack.VPackSlice;
import identity.hoprxi.core.domain.model.element.ResourceDescriptor;
import identity.hoprxi.core.domain.model.element.RoleDescriptor;
import identity.hoprxi.core.domain.model.id.Creator;
import identity.hoprxi.core.domain.model.permission.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import salt.hoprxi.id.ObjectId;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/***
 * @author <job href="www.hoprxi.com/authors/guan xiangHuan">guan xiangHuan</job>
 * @since JDK8.0
 * @version 0.0.1 2019-01-20
 */
public class ArangoDBPermissionRepository implements PermissionRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger(ArangoDBPermissionRepository.class);
    private static final EdgeUpdateOptions UPDATE_OPTIONS = new EdgeUpdateOptions().keepNull(false);
    private static Constructor<RoleDescriptor> ROLEDESCRIPTOR_CONSTRUCTOR;
    private static Constructor<ResourceDescriptor> RESOURCEDESCRIPTOR_CONSTRUCTOR;
    private static Constructor<Creator> CREATOR_CONSTRUCTOR;

    static {
        try {
            ROLEDESCRIPTOR_CONSTRUCTOR = RoleDescriptor.class.getDeclaredConstructor(String.class, String.class);
            ROLEDESCRIPTOR_CONSTRUCTOR.setAccessible(true);
            RESOURCEDESCRIPTOR_CONSTRUCTOR = ResourceDescriptor.class.getDeclaredConstructor(String.class, String.class, Creator.class);
            RESOURCEDESCRIPTOR_CONSTRUCTOR.setAccessible(true);
            CREATOR_CONSTRUCTOR = Creator.class.getDeclaredConstructor(String.class, String.class);
            CREATOR_CONSTRUCTOR.setAccessible(true);
        } catch (NoSuchMethodException e) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Not find matching constructor", e);
            }
        }
    }

    private ArangoDatabase identity = ArangoDBUtil.getResource().db("identity");

    @Override
    public void save(Permission permission) {
        boolean exist = identity.collection("processor").documentExists(permission.id());
        ArangoGraph graph = identity.graph("identity");
        ArangoEdgeCollection edge = graph.edgeCollection("processor");
        if (exist) {
            VertexEntity role = graph.vertexCollection("role").getVertex(permission.roleDescriptor().id(), VertexEntity.class);
            VertexEntity resource = graph.vertexCollection("resource").getVertex(permission.resourceDescriptor().id(), VertexEntity.class);
            if (permission.roleDescriptor().id().equals(role.getKey()) && permission.resourceDescriptor().id().equals(resource.getKey())) {
                edge.updateEdge(permission.id(), permission, UPDATE_OPTIONS);
            } else {
                edge.deleteEdge(permission.id());
                edge.insertEdge(new ProcessorEdge(role.getId(), resource.getId(), permission));
            }
        } else {
            VertexEntity role = graph.vertexCollection("role").getVertex(permission.roleDescriptor().id(), VertexEntity.class);
            VertexEntity resource = graph.vertexCollection("resource").getVertex(permission.resourceDescriptor().id(), VertexEntity.class);
            edge.insertEdge(new ProcessorEdge(role.getId(), resource.getId(), permission));
        }
    }

    private Permission rebuild(VPackSlice slice) throws IllegalAccessException, InvocationTargetException, InstantiationException {
        if (slice == null)
            return null;
        String id = slice.get("id").getAsString();
        String name = slice.get("name").getAsString();
        //role
        VPackSlice roleSlice = slice.get("role");
        RoleDescriptor roleDescriptor = ROLEDESCRIPTOR_CONSTRUCTOR.newInstance(roleSlice.get("id").getAsString(), roleSlice.get("name").getAsString());
        //processor
        Fuel fuel = Fuel.newFuel(slice.get("processor").get("fuel").get("formula").getAsString());
        Processor processor = new Processor(EngineManager.queryEngine(name), fuel);
        //schedule
        Schedule schedule = null;
        if (!slice.get("schedule").isNull()) {
            schedule = new Schedule(slice.get("schedule").get("cron").getAsString());
        }
        //resource
        VPackSlice resourceSlice = slice.get("resource");
        VPackSlice creatorSlice = resourceSlice.get("creator");
        Creator creator = CREATOR_CONSTRUCTOR.newInstance(creatorSlice.get("id").getAsString(), creatorSlice.get("username").getAsString());
        ResourceDescriptor resourceDescriptor = RESOURCEDESCRIPTOR_CONSTRUCTOR.newInstance(resourceSlice.get("id").getAsString(), resourceSlice.get("name").getAsString(), creator);
        return new Permission(id, name, roleDescriptor, schedule, processor, resourceDescriptor);
    }

    @Override
    public Collection<String> getNonRepetitivePermissionName() {
        final String query = "FOR v IN processor RETURN DISTINCT v.name";
        ArangoCursor<VPackSlice> slices = identity.query(query, VPackSlice.class);
        Set<String> set = new HashSet<>();
        while (slices.hasNext()) {
            set.add(slices.next().getAsString());
        }
        return set;
    }

    @Override
    public Permission[] findPermissionsFromUserAndPermissionNameAndResource(String userId, String permissionName, String resourceId) {
        final String query = " WITH user,role,resource\n " +
                "FOR v,e,p IN 1..2 OUTBOUND @user act,processor FILTER p.edges[1].name == @permissionName SORT p.edges[1].schedule DESC FILTER p.vertices[2]._key == @resourceId " +
                "RETURN {id:p.edges[1]._key,name:p.edges[1].name," +
                "role:{id:p.vertices[1]._key,name:p.vertices[1].name}," +
                "processor:{fuel:p.edges[1].processor.fuel}," +
                "schedule:p.edges[1].schedule," +
                "resource:{id:p.vertices[2]._key,name:p.vertices[2].name,creator:p.vertices[2].creator}}";
        Map<String, Object> bindVars = new MapBuilder().put("user", "user/" + userId).put("permissionName", permissionName).put("resourceId", resourceId).get();
        return findPermissions(query, bindVars);
    }

    private Permission[] findPermissions(String query, Map<String, Object> bindVars) {
        ArangoCursor<VPackSlice> slices = identity.query(query, bindVars, null, VPackSlice.class);
        List<Permission> permissionList = new ArrayList<>();
        try {
            while (slices.hasNext()) {
                permissionList.add(rebuild(slices.next()));
            }
        } catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("permission create fail", e);
            }
        }
        return permissionList.toArray(new Permission[permissionList.size()]);
    }

    @Override
    public void remove(String id) {
        ArangoGraph graph = identity.graph("identity");
        graph.edgeCollection("processor").deleteEdge(id);
    }

    @Override
    public String nextIdentity() {
        return new ObjectId().id();
    }

    private static class ProcessorEdge {
        @DocumentField(DocumentField.Type.KEY)
        private String id;
        @DocumentField(DocumentField.Type.FROM)
        private String from;
        @DocumentField(DocumentField.Type.TO)
        private String to;
        private Processor processor;
        private String name;
        private Schedule schedule;

        public ProcessorEdge(String from, String to, Permission permission) {
            this.from = from;
            this.to = to;
            this.id = permission.id();
            this.processor = permission.processor();
            this.name = permission.name();
            this.schedule = permission.schedule();
        }
    }
}
