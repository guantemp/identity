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
import identity.foxtail.core.domain.model.job.Command;
import identity.foxtail.core.domain.model.job.Schedule;
import identity.foxtail.core.domain.model.power.Power;
import identity.foxtail.core.domain.model.power.PowerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/***
 * @author <a href="www.foxtail.cc/authors/guan xiangHuan">guan xiangHuan</a>
 * @since JDK8.0
 * @version 0.0.1 2018-12-03
 */
public class ArangoDBPowerRepository implements PowerRepository {
    private static final Logger logger = LoggerFactory.getLogger(ArangoDBPowerRepository.class);
    private static final VertexUpdateOptions UPDATE_OPTIONS = new VertexUpdateOptions().keepNull(false);
    private ArangoDatabase identity = ArangoDBUtil.getDatabase();

    @Override
    public void save(Power power) {
        boolean exists = identity.collection("power").documentExists(power.id());
        if (exists) {

        } else {
            ArangoGraph graph = identity.graph("identity");
            VertexEntity role = graph.vertexCollection("role").getVertex(power.roleDescriptor().id(), VertexEntity.class);
            VertexEntity resource = graph.vertexCollection("resource").getVertex(power.resourceDescriptor().id(), VertexEntity.class);
            graph.edgeCollection("power").insertEdge(new PowerEdge(role.getId(), resource.getId(), power));
        }
    }

    @Override
    public Power find(String id) {
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
        private Command command;
        private Schedule schedule;
        @DocumentField(DocumentField.Type.FROM)
        private String from;

        @DocumentField(DocumentField.Type.TO)
        private String to;
        private Power power;

        public PowerEdge(String from, String to, Power power) {
            this.from = from;
            this.to = to;
            this.id = power.id();
            this.name = power.name();
            this.command = power.command();
            this.schedule = power.schedule();
        }
    }
}
