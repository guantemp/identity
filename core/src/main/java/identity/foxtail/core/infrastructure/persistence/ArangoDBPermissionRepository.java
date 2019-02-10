/*
 *  Copyright (c)2019 www.foxtail.cc All rights Reserved.
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

package identity.foxtail.core.infrastructure.persistence;

import com.arangodb.ArangoCursor;
import com.arangodb.ArangoDatabase;
import com.arangodb.ArangoGraph;
import com.arangodb.entity.DocumentField;
import com.arangodb.entity.VertexEntity;
import com.arangodb.model.DocumentUpdateOptions;
import com.arangodb.util.MapBuilder;
import com.arangodb.velocypack.VPackSlice;
import identity.foxtail.core.domain.model.element.ResourceDescriptor;
import identity.foxtail.core.domain.model.element.RoleDescriptor;
import identity.foxtail.core.domain.model.id.Creator;
import identity.foxtail.core.domain.model.permission.Permission;
import identity.foxtail.core.domain.model.permission.PermissionRepository;
import identity.foxtail.core.domain.model.permission.operate.EngineManager;
import identity.foxtail.core.domain.model.permission.operate.Operate;
import identity.foxtail.core.domain.model.permission.operate.Schedule;
import identity.foxtail.core.domain.model.permission.operate.Strategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/***
 * @author <job href="www.foxtail.cc/authors/guan xiangHuan">guan xiangHuan</job>
 * @since JDK8.0
 * @version 0.0.1 2019-01-20
 */
public class ArangoDBPermissionRepository implements PermissionRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger(ArangoDBPermissionRepository.class);
    private static final DocumentUpdateOptions UPDATE_OPTIONS = new DocumentUpdateOptions().keepNull(false);
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

    private ArangoDatabase identity = ArangoDBUtil.getDatabase();

    @Override
    public void save(Permission permission) {
       /*
        final String QUERY_PREFF = " WITH role,resource\n" +
            "FOR v,e,p IN 1..2 OUTBOUND @start operate FILTER p.vertices[1]._key == @resourceId " +
            "FILTER p.edges[0].permissionName.name == @permissionName AND p.edges[0].operate.name== @operateName " +
            "AND p.edges[0].operate.strategy.expression == @expression";
        StringBuilder builder = new StringBuilder(QUERY_PREFF);
        MapBuilder mapBuilder = new MapBuilder().put("start", "role/" + permission.roleDescriptor().id())
                .put("resourceId", permission.resourceDescriptor().id())
                .put("permissionName", permission.name())
                .put("operateName", permission.operate().name())
                .put("expression", permission.operate().strategy().expression());
        if (permission.operate().schedule() != null) {
            builder.append(" AND (p.edges[0].operate.schedule != null ? p.edges[0].operate.schedule.cron == @cron:true)");
            mapBuilder.put("cron", permission.operate().schedule().cron());
        }
        builder.append(" RETURN e");
        ArangoCursor<EdgeEntity> slices = identity.query(builder.toString(), mapBuilder.get(), null, EdgeEntity.class);
        if (slices.hasNext()) {
            EdgeEntity edge = slices.next();
            identity.collection("operate").updateDocument(edge.getKey(), permission, UPDATE_OPTIONS);
        }
        */
        boolean exist = identity.collection("operate").documentExists(permission.id());
        if (exist) {
            identity.collection("operate").updateDocument(permission.id(), permission, UPDATE_OPTIONS);
        } else {
            ArangoGraph graph = identity.graph("identity");
            VertexEntity role = graph.vertexCollection("role").getVertex(permission.roleDescriptor().id(), VertexEntity.class);
            VertexEntity resource = graph.vertexCollection("resource").getVertex(permission.resourceDescriptor().id(), VertexEntity.class);
            graph.edgeCollection("operate").insertEdge(new CommandEdge(role.getId(), resource.getId(), permission));
        }
    }

    private Permission rebuild(VPackSlice slice) throws IllegalAccessException, InvocationTargetException, InstantiationException {
        if (slice != null) {
            //role
            VPackSlice roleSlice = slice.get("role");
            RoleDescriptor roleDescriptor = ROLEDESCRIPTOR_CONSTRUCTOR.newInstance(roleSlice.get(DocumentField.Type.KEY.getSerializeName()).getAsString(), roleSlice.get("name").getAsString());
            //resource
            VPackSlice resourceSlice = slice.get("resource");
            VPackSlice creatorSlice = resourceSlice.get("creator");
            Creator creator = CREATOR_CONSTRUCTOR.newInstance(creatorSlice.get("id").getAsString(), creatorSlice.get("username").getAsString());
            ResourceDescriptor resourceDescriptor = RESOURCEDESCRIPTOR_CONSTRUCTOR.newInstance(resourceSlice.get(DocumentField.Type.KEY.getSerializeName()).getAsString(), resourceSlice.get("name").getAsString(), creator);
            //operate
            VPackSlice operateSlice = slice.get("operate");
            String id = operateSlice.get(DocumentField.Type.KEY.getSerializeName()).getAsString();
            String name = operateSlice.get("name").getAsString();
            VPackSlice strategySlice = slice.get("operate").get("operate").get("strategy");
            String formula = strategySlice.get("formula").getAsString();
            Strategy strategy = new Strategy(formula, EngineManager.queryEngine(name));
            Schedule schedule = null;
            if (!slice.get("operate").get("operate").get("schedule").isNone()) {
                VPackSlice scheduleSlice = slice.get("operate").get("operate").get("schedule");
                schedule = new Schedule(scheduleSlice.get("cron").getAsString());
            }
            Operate operate = new Operate(name, strategy, schedule);
            return new Permission(id, name, roleDescriptor, operate, resourceDescriptor);
        }
        return null;
    }


    @Override
    public Permission[] findPermissionWithRoleAndPermissionNameAndResource(String roleId, String permissionName, String resourceId) {
        final String query = " WITH role,resource\n " +
                "FOR v,e,p IN 1..2 OUTBOUND @role operate FILTER p.vertices[1]._key == @resourceId FILTER p.edges[0].name == @permissionName " +
                "SORT p.edges[0].operate.schedule DESC RETURN {role:p.vertices[0],operate:e,resource:p.vertices[1]}";
        Map<String, Object> bindVars = new MapBuilder().put("role", "role/" + roleId).put("resourceId", resourceId).
                put("permissionName", permissionName).get();
        return findPermissions(query, bindVars);
    }

    @Override
    public Permission[] findPermissionFromRoleWithPermissionName(String roleId, String permissionName) {
        final String query = " WITH role,resource\n " +
                "FOR v,e,p IN 1..2 OUTBOUND @role operate FILTER p.edges[0].name == @permissionName " +
                "SORT p.edges[0].operate.schedule DESC RETURN {role:p.vertices[0],operate:e,resource:p.vertices[1]}";
        Map<String, Object> bindVars = new MapBuilder().put("role", "role/" + roleId).put("permissionName", permissionName).get();
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

    }

    private static class CommandEdge {
        @DocumentField(DocumentField.Type.KEY)
        private String id;
        @DocumentField(DocumentField.Type.FROM)
        private String from;
        @DocumentField(DocumentField.Type.TO)
        private String to;
        private Operate operate;
        private String name;

        public CommandEdge(String from, String to, Permission permission) {
            this.from = from;
            this.to = to;
            this.id = permission.id();
            this.operate = permission.operate();
            this.name = permission.name();
        }
    }
}
