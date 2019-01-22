/*
 * Copyright (c) 2019 www.foxtail.cc All rights Reserved.
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

package identity.foxtail.core.infrastructure.persistence;

import com.arangodb.ArangoDatabase;
import com.arangodb.ArangoGraph;
import com.arangodb.entity.DocumentField;
import com.arangodb.entity.VertexEntity;
import com.arangodb.model.VertexUpdateOptions;
import identity.foxtail.core.domain.model.privilege.Job;
import identity.foxtail.core.domain.model.privilege.PermitPrivilege;
import identity.foxtail.core.domain.model.privilege.PermitPrivilegeRepository;
import identity.foxtail.core.domain.model.privilege.Schedule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/***
 * @author <a href="www.foxtail.cc/authors/guan xiangHuan">guan xiangHuan</a>
 * @since JDK8.0
 * @version 0.0.1 2019-01-22
 */
public class ArangoDBPermitPrivilegeRepository implements PermitPrivilegeRepository {
    private static final Logger logger = LoggerFactory.getLogger(ArangoDBPermitPrivilegeRepository.class);
    private static final VertexUpdateOptions UPDATE_OPTIONS = new VertexUpdateOptions().keepNull(false);
    private ArangoDatabase identity = ArangoDBUtil.getDatabase();

    @Override
    public void save(PermitPrivilege permitPrivilege) {
        boolean exists = identity.collection("privilege").documentExists(permitPrivilege.id());
        if (exists) {

        } else {
            ArangoGraph graph = identity.graph("identity");
            VertexEntity role = graph.vertexCollection("role").getVertex(permitPrivilege.roleDescriptor().id(), VertexEntity.class);
            VertexEntity resource = graph.vertexCollection("resource").getVertex(permitPrivilege.resourceDescriptor().id(), VertexEntity.class);
            graph.edgeCollection("privilege").insertEdge(new PermitPprivilegeEdge(role.getId(), resource.getId(), permitPrivilege));
        }
    }

    @Override
    public void remove(String id) {

    }

    @Override
    public PermitPrivilege find(String id) {
        return null;
    }

    @Override
    public PermitPrivilege validPermitPrivilegrFromResourceAndRole() {
        return null;
    }

    private static class PermitPprivilegeEdge {
        @DocumentField(DocumentField.Type.KEY)
        private String id;
        @DocumentField(DocumentField.Type.FROM)
        private String from;
        @DocumentField(DocumentField.Type.TO)
        private String to;
        private Job job;
        private Schedule schedule;

        PermitPprivilegeEdge(String from, String to, PermitPrivilege permitPrivilege) {
            this.from = from;
            this.to = to;
            this.id = permitPrivilege.id();
            this.schedule = permitPrivilege.schedule();
            this.job = permitPrivilege.job();
        }
    }
}
