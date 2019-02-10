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

package identity.foxtail.core.domain.model.permission;

import identity.foxtail.core.domain.model.element.Resource;
import identity.foxtail.core.domain.model.element.ResourceRepository;
import identity.foxtail.core.domain.model.element.Role;
import identity.foxtail.core.domain.model.element.RoleRepository;
import identity.foxtail.core.domain.model.id.*;
import identity.foxtail.core.domain.model.permission.operate.EngineManager;
import identity.foxtail.core.domain.model.permission.operate.Operate;
import identity.foxtail.core.domain.model.permission.operate.Schedule;
import identity.foxtail.core.domain.model.permission.operate.Strategy;
import identity.foxtail.core.infrastructure.persistence.*;
import org.junit.BeforeClass;
import org.junit.Test;

/***
 * @author <a href="www.foxtail.cc/authors/guan xiangHuan">guan xiangHuan</a>
 * @since JDK8.0
 * @version 0.0.1 2019-01-22
 */
public class PermissionRepositoryTest {
    private static final RoleRepository roleRepository = new ArangoDBRoleRepository();
    private static final GroupRepository groupRepository = new ArangoDBGroupRepository();
    private static final UserRepository userRepository = new ArangoDBUserRepository();
    private static final GroupMemberService service = new GroupMemberService(groupRepository);
    private static final ResourceRepository resourceRepository = new ArangoDBResourceRepository();
    private static final PermissionRepository repo = new ArangoDBPermissionRepository();

    @BeforeClass
    public static void setUpBeforeClass() {
        Role casher = new Role("casher", "收銀員", "就是收钱的");
        User Son_Goku = new User("Son_Goku", "孫悟空", "中文密碼也是可以的", "0830-2135679", Enablement.FOREVER);
        userRepository.save(Son_Goku);
        casher.assignUser(Son_Goku);
        roleRepository.save(casher);
        User Zhu_Bajie = new User("Zhu_Bajie", "猪八戒", "可以的，好幸福et", "13679692301", Enablement.FOREVER);
        casher.assignUser(Zhu_Bajie);
        userRepository.save(Zhu_Bajie);
        roleRepository.save(casher);

        Resource box = new Resource("box", "錢箱", Zhu_Bajie.toCreator());
        resourceRepository.save(box);
        Operate open = new Operate("open", Strategy.NO_STRATEGY);
        Permission permission = new Permission("6666", "open_box", casher.toRoleDescriptor(), open, box.toResourceDescriptor());
        repo.save(permission);

        Resource catalog = new Resource("catalog", "产品目录", Son_Goku.toCreator());
        resourceRepository.save(catalog);
        Resource fresh = new Resource("fresh", "生鲜", Son_Goku.toCreator());
        fresh.assignTo(catalog);
        resourceRepository.save(fresh);
        Resource meat = new Resource("meat", "鲜肉", Zhu_Bajie.toCreator());
        meat.assignTo(fresh);
        resourceRepository.save(meat);
        Resource sku = new Resource("sku", "二刀肉", Zhu_Bajie.toCreator());
        sku.assignTo(meat);
        resourceRepository.save(sku);

        Operate discount = new Operate("discount", new Strategy("rate>=20", EngineManager.queryEngine("discount")));
        Permission discountPermission = new Permission("6767", "DISCOUNT", casher.toRoleDescriptor(), discount, sku.toResourceDescriptor());
        repo.save(discountPermission);
        discount = new Operate("discount", new Strategy("rate>=40", EngineManager.queryEngine("discount")),
                new Schedule("* 15 12 33"));
        discountPermission = new Permission("6868", "DISCOUNT", casher.toRoleDescriptor(), discount, sku.toResourceDescriptor());
        repo.save(discountPermission);
        discount = new Operate("discount", new Strategy("rate>=40", EngineManager.queryEngine("discount")),
                new Schedule("* 12 12 33"));
        discountPermission = new Permission("6969", "DISCOUNT", casher.toRoleDescriptor(), discount, sku.toResourceDescriptor());
        repo.save(discountPermission);
        Operate red = new Operate("red", new Strategy("value<=45.00", EngineManager.queryEngine("red_catalog")));
        Permission redPermission = new Permission("7777", "red_catalog", casher.toRoleDescriptor(), red, meat.toResourceDescriptor());
        repo.save(redPermission);
    }

    @Test
    public void findPermissionWithRoleAndPermissionNameAndResource() {
        Permission[] permissions = repo.findPermissionWithRoleAndPermissionNameAndResource("casher", "DISCOUNT", "sku");
        for (Permission permission : permissions) {
            System.out.println(permission);
        }
    }
}