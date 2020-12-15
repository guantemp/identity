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

import com.arangodb.ArangoDatabase;
import com.arangodb.ArangoGraph;
import com.arangodb.entity.DocumentEntity;
import com.arangodb.entity.DocumentField;
import com.arangodb.entity.VertexEntity;
import com.arangodb.model.VertexUpdateOptions;
import com.arangodb.velocypack.VPackSlice;
import identity.hoprxi.core.domain.model.id.Socialization;
import identity.hoprxi.core.domain.model.id.SocializationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * @author <a href="www.hoprxi.com/author/guan xianghuang">guan xiangHuan</a>
 * @version 0.0.1 2020-12-15
 * @since JDK8.0
 */
public class ArangoDBSocializationRepository implements SocializationRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger(ArangoDBSocializationRepository.class);
    private static final VertexUpdateOptions UPDATE_OPTIONS = new VertexUpdateOptions().keepNull(false);
    private static Constructor<Socialization> socializationConstructor;

    static {
        try {
            socializationConstructor = Socialization.class.getDeclaredConstructor(String.class, String.class, Socialization.ThirdParty.class);
            socializationConstructor.setAccessible(true);
        } catch (NoSuchMethodException e) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Not find such constructor for Socialization", e);
            }
        }
    }

    private final ArangoDatabase identity;

    public ArangoDBSocializationRepository(String databaseName) {
        identity = ArangoDBUtil.getResource().db(databaseName);
    }

    @Override
    public void save(Socialization socialization) {
        ArangoGraph graph = identity.graph("identity");
        boolean exists = identity.collection("socialization").documentExists(socialization.unionId());
        if (!exists) {
            VertexEntity socializationVertex = graph.vertexCollection("socialization").insertVertex(socialization);
            insertBindEdgeOfBrand(graph, socializationVertex, socialization.userId());
        }
    }

    private void insertBindEdgeOfBrand(ArangoGraph graph, DocumentEntity itemVertex, String userId) {
        VertexEntity userVertex = graph.vertexCollection("user").getVertex(userId, VertexEntity.class);
        graph.edgeCollection("bing").insertEdge(new BindEdge(itemVertex.getId(), userVertex.getId()));
    }

    @Override
    public Socialization find(String unionId) {
        ArangoGraph graph = identity.graph("identity");
        VPackSlice slice = graph.vertexCollection("socialization").getVertex(unionId, VPackSlice.class);
        if (slice != null) {
            try {
                String id = slice.get(DocumentField.Type.KEY.getSerializeName()).getAsString();
                String userId = slice.get("userid").getAsString();
                Socialization.ThirdParty thirdParty = Socialization.ThirdParty.valueOf(slice.get("thirdParty").getAsString());
                Socialization socialization = socializationConstructor.newInstance(id, userId, thirdParty);
                return socialization;
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Can't rebuild Socialization", e);
                }
            }
        }
        return null;
    }

    @Override
    public void remove(String unionId) {
        ArangoGraph graph = identity.graph("identity");
        graph.vertexCollection("socialization").deleteVertex(unionId);
    }

    private static class BindEdge {
        @DocumentField(DocumentField.Type.FROM)
        private String from;
        @DocumentField(DocumentField.Type.TO)
        private String to;

        private BindEdge(String from, String to) {
            this.from = from;
            this.to = to;
        }
    }
}

