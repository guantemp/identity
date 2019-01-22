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

package identity.foxtail.core.domain.model.element;

import identity.foxtail.core.domain.model.id.Creator;
import identity.foxtail.core.domain.model.id.User;
import identity.foxtail.core.domain.model.id.UserRepository;
import identity.foxtail.core.infrastructure.persistence.ArangoDBResourceRepository;
import identity.foxtail.core.infrastructure.persistence.ArangoDBUserRepository;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class ResourceRepositoryTest {
    private static UserRepository userRepository = new ArangoDBUserRepository();
    private static ResourceRepository resourceRepository = new ArangoDBResourceRepository();
    private static Creator creator;

    @BeforeClass
    public static void setUp() {
        User admin = new User("admin", "管理员", "ERSdgre7783", "0830-2517210");
        userRepository.save(admin);
        creator = admin.toCreator();

        Resource root = new Resource("root", "全部区域", creator);
        resourceRepository.save(root);

        Resource sichuan = new Resource("sichuan", "四川", creator);
        sichuan.assignTo(root);
        resourceRepository.save(sichuan);
        Resource chengdu = new Resource("chengdu", "成都", creator);
        chengdu.assignTo(sichuan);
        resourceRepository.save(chengdu);
        Resource leshan = new Resource("leshan", "乐山", creator);
        leshan.assignTo(sichuan);
        resourceRepository.save(leshan);
        Resource luzhou = new Resource("luzhou", "泸州", creator);
        luzhou.assignTo(sichuan);
        resourceRepository.save(luzhou);
        Resource lmt = new Resource("lmt", "龙马潭", creator);
        lmt.assignTo(luzhou);
        resourceRepository.save(lmt);
    }

    @AfterClass
    public static void tearDown() {
        userRepository.remove("admin");
        resourceRepository.remove("lmt");
        resourceRepository.remove("luzhou");
        resourceRepository.remove("leshan");
        resourceRepository.remove("chengdu");
        resourceRepository.remove("sichuan");
        resourceRepository.remove("root");
    }

    @Test
    public void select() throws Exception {
        Resource[] root = resourceRepository.root();
        Assert.assertEquals(1, root.length);
        Resource[] child = resourceRepository.children("sichuan");
        Assert.assertEquals(3, child.length);

        resourceRepository.remove("sichuan");
        Resource sichuan = resourceRepository.find("sichuan");
        Assert.assertNotNull(sichuan);
        User admin = userRepository.find("admin");
        Assert.assertTrue(sichuan.isCreator(admin));

        Resource luzhou = resourceRepository.find("luzhou");
        System.out.println(luzhou);
        luzhou.rename("管得泸州");
        luzhou.unassign();
        resourceRepository.save(luzhou);
        luzhou = resourceRepository.find("luzhou");
        Assert.assertEquals("管得泸州", luzhou.name());

        root = resourceRepository.root();
        Assert.assertEquals(2, root.length);
        Resource parent = resourceRepository.parent("sichuan");
        Assert.assertEquals("root", parent.id());
        parent = resourceRepository.parent("root");
        Assert.assertNull(parent);
    }
}