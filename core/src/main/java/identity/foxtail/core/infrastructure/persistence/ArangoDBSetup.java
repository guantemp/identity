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

import com.arangodb.ArangoDB;
import com.arangodb.ArangoDatabase;
import com.arangodb.ArangoGraph;
import com.arangodb.entity.CollectionType;
import com.arangodb.entity.EdgeDefinition;
import com.arangodb.entity.KeyType;
import com.arangodb.entity.VertexEntity;
import com.arangodb.model.CollectionCreateOptions;
import com.arangodb.model.HashIndexOptions;
import com.arangodb.velocypack.VPackSlice;
import identity.foxtail.core.domain.model.id.Enablement;
import identity.foxtail.core.domain.model.id.Group;
import identity.foxtail.core.domain.model.id.User;
import mi.foxtail.id.Identity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

/***
 * @author <a href="www.foxtail.cc/authors/guan xianghuang">guan xiangHuan</a>
 * @since JDK8.0
 * @version 0.0.2 2018-11-13
 */

public class ArangoDBSetup {
    private static final Logger logger = LoggerFactory.getLogger(ArangoDBSetup.class);

    public static void main(String[] args) {
        ArangoDBSetup.setup("identity");
    }

    public static void setup(String databaseName) {
        ArangoDB arangoDB = ArangoDBUtil.getResource();
        if (arangoDB.db(databaseName).exists()) {
            arangoDB.db(databaseName).drop();
            logger.info("{} be discarded", databaseName);
        }
        arangoDB.createDatabase(databaseName);
        ArangoDatabase db = arangoDB.db(databaseName);
        //vertex
        CollectionCreateOptions vertexOptions = new CollectionCreateOptions();
        vertexOptions.keyOptions(true, KeyType.traditional, 1, 1);
        for (String s : new String[]{"resource", "role", "group", "user"}) {
            db.createCollection(s, vertexOptions);
        }
        //index
        Collection<String> index = new ArrayList<>();
        //SkiplistIndexOptions indexOptions = new SkiplistIndexOptions().sparse(true);
        index.add("username");
        HashIndexOptions hashIndexOptions = new HashIndexOptions().unique(true);
        db.collection("user").ensureHashIndex(index, hashIndexOptions);
        //
        index.clear();
        index.add("name");
        db.collection("group").ensureHashIndex(index, hashIndexOptions);
        db.collection("role").ensureHashIndex(index, hashIndexOptions);
        //other index
        index.clear();
        index.add("telephoneNumber");
        db.collection("user").ensureHashIndex(index, hashIndexOptions);
        //edge
        CollectionCreateOptions edgeOptions = new CollectionCreateOptions().type(CollectionType.EDGES);
        for (String s : new String[]{"subordinate", "act", "create"}) {
            db.createCollection(s, edgeOptions);
        }
        edgeOptions.keyOptions(true, KeyType.traditional, 1, 1);
        db.createCollection("power", edgeOptions);
        //graph
        Collection<EdgeDefinition> list = new ArrayList<>();
        list.add(new EdgeDefinition().collection("create").from("user").to("resource"));
        list.add(new EdgeDefinition().collection("act").from("group", "user").to("role"));
        list.add(new EdgeDefinition().collection("subordinate").from("group", "resource").to("group", "user", "resource"));
        list.add(new EdgeDefinition().collection("power").from("role").to("resource"));
        db.createGraph(databaseName, list);
        arangoDB.shutdown();
        logger.info("{} be created", databaseName);
    }

    protected static void init() {
        long start = System.currentTimeMillis();
        ArangoDB arangoDB = ArangoDBUtil.getResource();
        ArangoDatabase database = arangoDB.db("identity");
        ArangoGraph graph = database.graph("identity");
        Group group = new Group("1", "管理员组", "具有所有权限");
        VertexEntity g = arangoDB.db("identity").graph("identity").vertexCollection("group")
                .insertVertex(group);
        group = new Group("2", "一般人员组", "具有基本权限");
        VertexEntity g1 = arangoDB.db("identity").graph("identity").vertexCollection("group")
                .insertVertex(group);
        //graph.edgeCollection("contain").insertEdge(new ArangoDBGroupRepository.ActEdge(g.getId(), g1.getId()));

        for (int i = 0; i < 8; i++) {
            User user = new User(String.valueOf(Identity.generate()),
                    "Qwe1234" + i, "测试中文" + i, "Qwe1234" + i, new Enablement(true, LocalDateTime.now().plusDays(45)));
            VertexEntity du = graph.vertexCollection("user").insertVertex(user);
            //graph.edgeCollection("contain").insertEdge(new ArangoDBGroupRepository.ActEdge(g1.getId(), du.getId()));
        }

        VertexEntity eu = graph.vertexCollection("user").insertVertex(User.ANONYMOUS);
        //graph.edgeCollection("contain").insertEdge(new ArangoDBGroupRepository.ActEdge(g1.getId(), eu.getId()));

        User test = new User("admin", "Qwe1234", "管理员", "admin", Enablement.FOREVER);
        eu = graph.vertexCollection("user").insertVertex(test);
        // EdgeEntity edge = graph.edgeCollection("contain").insertEdge(new ArangoDBGroupRepository.ActEdge(g.getId(), eu.getId()));
        VPackSlice slice = graph.vertexCollection("user").getVertex("admin", VPackSlice.class);
        eu = graph.vertexCollection("user").getVertex("admin", VertexEntity.class);
        System.out.println("find: " + slice);
        System.out.println(arangoDB.db("identity").collection("user").documentExists("admin"));

        graph.vertexCollection("user").deleteVertex(User.ANONYMOUS.username());
        System.out.println(arangoDB.db("identity").collection("user").documentExists(User.ANONYMOUS.username()));

        arangoDB.shutdown();
        arangoDB = null;
        System.out.println("Excute:" + (System.currentTimeMillis() - start));
    }
}
