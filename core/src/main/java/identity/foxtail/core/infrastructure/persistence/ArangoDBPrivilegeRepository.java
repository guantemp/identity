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

package identity.foxtail.core.infrastructure.persistence;

import com.arangodb.ArangoDatabase;
import com.arangodb.ArangoGraph;
import com.arangodb.entity.DocumentField;
import com.arangodb.entity.VertexEntity;
import com.arangodb.model.VertexUpdateOptions;
import identity.foxtail.core.domain.model.privilege.Job;
import identity.foxtail.core.domain.model.privilege.Privilege;
import identity.foxtail.core.domain.model.privilege.PrivilegeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/***
 * @author <a href="www.foxtail.cc/authors/guan xiangHuan">guan xiangHuan</a>
 * @since JDK8.0
 * @version 0.0.1 2018-12-03
 */
public class ArangoDBPrivilegeRepository implements PrivilegeRepository {
    private static final Logger logger = LoggerFactory.getLogger(ArangoDBPrivilegeRepository.class);
    private static final VertexUpdateOptions UPDATE_OPTIONS = new VertexUpdateOptions().keepNull(false);
    private ArangoDatabase identity = ArangoDBUtil.getDatabase();

    @Override
    public void save(Privilege privilege) {
        boolean exists = identity.collection("privilege").documentExists(privilege.id());
        if (exists) {

        } else {
            ArangoGraph graph = identity.graph("identity");
            VertexEntity role = graph.vertexCollection("role").getVertex(privilege.roleDescriptor().id(), VertexEntity.class);
            VertexEntity resource = graph.vertexCollection("resource").getVertex(privilege.resourceDescriptor().id(), VertexEntity.class);
            graph.edgeCollection("privilege").insertEdge(new PowerEdge(role.getId(), resource.getId(), privilege));
        }
    }

    @Override
    public Privilege find(String id) {
        return null;
    }

    @Override
    public String nextIdentity() {
        return null;
    }

    @Override
    public void remove(String id) {

    }

    private static class PowerEdge {
        @DocumentField(DocumentField.Type.KEY)
        private String id;
        private String name;
        private Job job;
        @DocumentField(DocumentField.Type.FROM)
        private String from;

        @DocumentField(DocumentField.Type.TO)
        private String to;
        private Privilege privilege;

        public PowerEdge(String from, String to, Privilege privilege) {
            this.from = from;
            this.to = to;
            this.id = privilege.id();
            this.name = privilege.name();
            this.job = privilege.command();
        }
    }
}
