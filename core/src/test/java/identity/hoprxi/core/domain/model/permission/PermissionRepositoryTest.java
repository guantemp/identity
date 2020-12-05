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

package identity.hoprxi.core.domain.model.permission;

import identity.hoprxi.core.domain.model.element.Resource;
import identity.hoprxi.core.domain.model.element.ResourceRepository;
import identity.hoprxi.core.domain.model.element.Role;
import identity.hoprxi.core.domain.model.element.RoleRepository;
import identity.hoprxi.core.domain.model.id.Enablement;
import identity.hoprxi.core.domain.model.id.User;
import identity.hoprxi.core.domain.model.id.UserRepository;
import identity.hoprxi.core.domain.servers.AuthorizationService;
import identity.hoprxi.core.infrastructure.persistence.ArangoDBPermissionRepository;
import identity.hoprxi.core.infrastructure.persistence.ArangoDBResourceRepository;
import identity.hoprxi.core.infrastructure.persistence.ArangoDBRoleRepository;
import identity.hoprxi.core.infrastructure.persistence.ArangoDBUserRepository;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Collection;

/***
 * @author <a href="www.hoprxi.com/authors/guan xiangHuan">guan xiangHuan</a>
 * @since JDK8.0
 * @version 0.0.1 2019-01-22
 */
public class PermissionRepositoryTest {
    private static final RoleRepository roleRepository = new ArangoDBRoleRepository();
    private static final UserRepository userRepository = new ArangoDBUserRepository("identity");
    private static final ResourceRepository resourceRepository = new ArangoDBResourceRepository();
    private static final PermissionRepository repo = new ArangoDBPermissionRepository();
    private static final AuthorizationService authorizationService = new AuthorizationService(repo, resourceRepository);

    @BeforeClass
    public static void setUpBeforeClass() {
        Role cashier = new Role("cashier", "收银员", "就是收钱的");

        User Bailong = new User("Bailong", "白龙马", "@ewt56gh", "18982455058", Enablement.FOREVER);
        userRepository.save(Bailong);

        User Sand_Monk = new User("Sand_Monk", "沙僧", ")dgdf324", "18982455057", Enablement.FOREVER);
        userRepository.save(Sand_Monk);
        cashier.assignUser(Sand_Monk);
        roleRepository.save(cashier);

        User Zhu_Bajie = new User("Zhu_Bajie", "猪八戒", "Wfhf456", "13679692301", Enablement.FOREVER);
        cashier.assignUser(Zhu_Bajie);
        userRepository.save(Zhu_Bajie);
        roleRepository.save(cashier);

        Role cashierSupr = new Role("cashierSupr", "收银主管", "收银员的老大");
        User Son_Goku = new User("Sun_WuKong", "孫悟空", "%dfhdfs82", "0830-2135679", Enablement.FOREVER);
        cashierSupr.assignUser(Son_Goku);
        userRepository.save(Son_Goku);
        roleRepository.save(cashierSupr);

        Role CFO = new Role("CFO", "财务总监", "所有财政权力管理，对董事会负责");
        User tang = new User("tang", "唐僧", "wtfgvvdgdf", "18982455056", Enablement.FOREVER);
        CFO.assignUser(tang);
        userRepository.save(tang);
        roleRepository.save(CFO);
        //open box
        Resource box = new Resource("box", "錢箱", Zhu_Bajie.toCreator());
        resourceRepository.save(box);
        Processor open = new Processor(EngineManager.queryEngine("open_box"), Fuel.LUBRICANT);
        Permission permission = new Permission("6666", "打开钱箱", cashier.toRoleDescriptor(), open, box.toResourceDescriptor());
        repo.save(permission);
        permission = new Permission("6667", "打开钱箱", cashierSupr.toRoleDescriptor(), open, box.toResourceDescriptor());
        repo.save(permission);
        //refund
        Resource catalog = new Resource("catalog", "产品目录", Son_Goku.toCreator());
        resourceRepository.save(catalog);

        Processor refund = new Processor(EngineManager.queryEngine("refund"), Fuel.LUBRICANT);
        permission = new Permission("6668", "退货", cashierSupr.toRoleDescriptor(), refund, catalog.toResourceDescriptor());
        repo.save(permission);

        Resource fresh = new Resource("fresh", "生鲜", Son_Goku.toCreator());
        fresh.assignTo(catalog);
        resourceRepository.save(fresh);
        Resource meat = new Resource("meat", "鲜肉", Zhu_Bajie.toCreator());
        meat.assignTo(fresh);
        resourceRepository.save(meat);
        Resource sku = new Resource("sku", "二刀肉", Zhu_Bajie.toCreator());
        sku.assignTo(meat);
        resourceRepository.save(sku);

        Processor discount = new Processor(EngineManager.queryEngine("discount"), new Fuel("rate>=20"));
        Permission discountPermission = new Permission("6767", "discount", cashier.toRoleDescriptor(), discount, sku.toResourceDescriptor());
        repo.save(discountPermission);
        discount = new Processor(EngineManager.queryEngine("discount"), new Fuel("rate>=40"));
        discountPermission = new Permission("6868", "discount", cashier.toRoleDescriptor(), new Schedule("* 15 12 33"), discount, sku.toResourceDescriptor());
        repo.save(discountPermission);
        discountPermission = new Permission("6969", "discount", cashier.toRoleDescriptor(), new Schedule("* 18 12 33"), discount, sku.toResourceDescriptor());
        repo.save(discountPermission);
        discount = new Processor(EngineManager.queryEngine("discount"), new Fuel("rate>=60"));
        discountPermission = new Permission("696969", "discount", cashier.toRoleDescriptor(), discount, fresh.toResourceDescriptor());
        repo.save(discountPermission);

        Processor red = new Processor(EngineManager.queryEngine("red_catalog"), new Fuel("value<=45.00"));
        Permission redPermission = new Permission("7777", "red_catalog", cashier.toRoleDescriptor(), red, meat.toResourceDescriptor());
        repo.save(redPermission);

    }

    @AfterClass
    public static void teardown() {

    }

    @Test
    public void findPermissions() {
    }

    @Test
    public void getNonRepetitivePermissionName() {
        Collection<String> names = repo.getNonRepetitivePermissionName();
        for (String name : names)
            System.out.println(name);
    }
}