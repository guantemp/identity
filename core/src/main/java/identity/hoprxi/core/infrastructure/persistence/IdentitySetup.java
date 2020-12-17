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

import com.arangodb.ArangoDB;
import com.arangodb.ArangoDatabase;
import com.arangodb.ArangoGraph;
import com.arangodb.entity.EdgeDefinition;
import com.arangodb.entity.KeyType;
import com.arangodb.entity.VertexEntity;
import com.arangodb.model.CollectionCreateOptions;
import com.arangodb.model.PersistentIndexOptions;
import com.arangodb.velocypack.VPackSlice;
import identity.hoprxi.core.domain.model.id.Enablement;
import identity.hoprxi.core.domain.model.id.Group;
import identity.hoprxi.core.domain.model.id.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import salt.hoprxi.id.LongId;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

/***
 * @author <a href="www.hoprxi.com/authors/guan xianghuang">guan xiangHuan</a>
 * @since JDK8.0
 * @version 0.0.3 2020-12-13
 */

public class IdentitySetup {
    private static final Logger logger = LoggerFactory.getLogger(IdentitySetup.class);

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
        for (String s : new String[]{"resource", "role", "group", "user", "socialization"}) {
            db.createCollection(s, vertexOptions);
        }
        //index
        Collection<String> index = new ArrayList<>();
        PersistentIndexOptions persistentIndexOptions = new PersistentIndexOptions().unique(true);
        //SkiplistIndexOptions indexOptions = new SkiplistIndexOptions().sparse(true);
        index.add("username");
        index.add("telephoneNumber");
        index.add("email");
        db.collection("user").ensurePersistentIndex(index, persistentIndexOptions);
        //index
        index.clear();
        index.add("name");
        db.collection("group").ensurePersistentIndex(index, persistentIndexOptions);
        db.collection("role").ensurePersistentIndex(index, persistentIndexOptions);
        /*
        //edge
        CollectionCreateOptions edgeOptions = new CollectionCreateOptions().type(CollectionType.EDGES);
        for (String s : new String[]{"subordinate", "act", "create","processor"}) {
            db.createCollection(s, edgeOptions);
        }
        edgeOptions.keyOptions(true, KeyType.traditional, 1, 1);
        db.createCollection("processor", edgeOptions);
         */
        //graph
        Collection<EdgeDefinition> list = new ArrayList<>();
        list.add(new EdgeDefinition().collection("create").from("user").to("resource"));
        list.add(new EdgeDefinition().collection("act").from("group", "user").to("role"));
        list.add(new EdgeDefinition().collection("subordinate").from("group", "resource").to("group", "user", "resource"));
        list.add(new EdgeDefinition().collection("processor").from("role").to("resource"));
        list.add(new EdgeDefinition().collection("bind").from("socialization").to("user"));
        db.createGraph("identity", list);
        arangoDB.shutdown();
        logger.info("{} be created", databaseName);
    }

    public static void init() {
        long start = System.currentTimeMillis();
        ArangoDB arangoDB = ArangoDBUtil.getResource();
        ArangoDatabase database = arangoDB.db("identity");
        ArangoGraph graph = database.graph("identity");
        Group group = new Group("1", "管理员组", "具有所有权限");
        arangoDB.db("identity").graph("identity").vertexCollection("group").insertVertex(group);
        new Group("2", "一般人员组", "具有基本权限");
        arangoDB.db("identity").graph("identity").vertexCollection("group")
                .insertVertex(group);
        //graph.edgeCollection("contain").insertEdge(new ArangoDBGroupRepository.ActEdge(g.getId(), g1.getId()));

        for (int i = 0; i < 8; i++) {
            User user = new User(String.valueOf(LongId.generate()),
                    "Qwe1234" + i, "测试中文" + i, "Qwe1234" + i, null, new Enablement(true, LocalDateTime.now().plusDays(45)));
            VertexEntity du = graph.vertexCollection("user").insertVertex(user);
            //graph.edgeCollection("contain").insertEdge(new ArangoDBGroupRepository.ActEdge(g1.getId(), du.getId()));
        }

        VertexEntity eu = graph.vertexCollection("user").insertVertex(User.ANONYMOUS);
        //graph.edgeCollection("contain").insertEdge(new ArangoDBGroupRepository.ActEdge(g1.getId(), eu.getId()));

        User test = new User("admin", "Qwe1234", "管理员", "13679692305", null, Enablement.PERMANENCE);
        eu = graph.vertexCollection("user").insertVertex(test);
        // EdgeEntity edge = graph.edgeCollection("contain").insertEdge(new ArangoDBGroupRepository.ActEdge(g.getId(), eu.getId()));
        VPackSlice slice = graph.vertexCollection("user").getVertex("admin", VPackSlice.class);
        eu = graph.vertexCollection("user").getVertex("admin", VertexEntity.class);
        System.out.println("find: " + slice);
        System.out.println(arangoDB.db("identity").collection("user").documentExists("admin"));

        graph.vertexCollection("user").deleteVertex(User.ANONYMOUS.username());
        System.out.println(arangoDB.db("identity").collection("user").documentExists(User.ANONYMOUS.username()));

        arangoDB.shutdown();
        System.out.println("Excute:" + (System.currentTimeMillis() - start));
    }
}
