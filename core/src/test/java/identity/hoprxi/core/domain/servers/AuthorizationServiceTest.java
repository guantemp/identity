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

package identity.hoprxi.core.domain.servers;

import identity.hoprxi.core.domain.model.element.Resource;
import identity.hoprxi.core.domain.model.element.ResourceRepository;
import identity.hoprxi.core.domain.model.element.Role;
import identity.hoprxi.core.domain.model.element.RoleRepository;
import identity.hoprxi.core.domain.model.id.Enablement;
import identity.hoprxi.core.domain.model.id.User;
import identity.hoprxi.core.domain.model.id.UserRepository;
import identity.hoprxi.core.domain.model.permission.*;
import identity.hoprxi.core.infrastructure.persistence.ArangoDBPermissionRepository;
import identity.hoprxi.core.infrastructure.persistence.ArangoDBResourceRepository;
import identity.hoprxi.core.infrastructure.persistence.ArangoDBRoleRepository;
import identity.hoprxi.core.infrastructure.persistence.ArangoDBUserRepository;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/***
 * @author <a href="www.hoprxi.com/authors/guan xiangHuan">guan xiangHuan</a>
 * @since JDK8.0
 * @version 0.0.1 2019-03-04
 */
public class AuthorizationServiceTest {
    private static final RoleRepository roleRepository = new ArangoDBRoleRepository();
    private static final UserRepository userRepository = new ArangoDBUserRepository("identity");
    private static final ResourceRepository resourceRepository = new ArangoDBResourceRepository();
    private static final PermissionRepository permissionRepository = new ArangoDBPermissionRepository();
    private static final AuthorizationService authorizationService = new AuthorizationService(permissionRepository, resourceRepository);

    @BeforeClass
    public static void setUpBeforeClass() {
        User Bailong = new User("Bailong", "白龙马", "@ewt56gh", "18982455058", null, Enablement.PERMANENCE);
        userRepository.save(Bailong);

        Role cashier = new Role("cashier", "收银员", "收钱的");
        User Sand_Monk = new User("Sand_Monk", "沙僧", ")dgdf324", "18982455057", null, Enablement.PERMANENCE);
        userRepository.save(Sand_Monk);
        cashier.assignUser(Sand_Monk);
        roleRepository.save(cashier);
        User Zhu_Bajie = new User("Zhu_Bajie", "猪八戒", "Wfhf456", "13679692301", null, Enablement.PERMANENCE);
        cashier.assignUser(Zhu_Bajie);
        userRepository.save(Zhu_Bajie);
        roleRepository.save(cashier);

        Role cashierSupr = new Role("cashierSupr", "收银主管", "收银员的老大");
        User Son_Goku = new User("Sun_WuKong", "孫悟空", "%dfhdfs26", "0830-2135679", null, Enablement.PERMANENCE);
        cashierSupr.assignUser(Son_Goku);
        userRepository.save(Son_Goku);
        roleRepository.save(cashierSupr);
        cashier.assignUser(Son_Goku);
        roleRepository.save(cashier);

        Role CFO = new Role("CFO", "财务总监", "财政一把手，对董事会负责");
        User tang = new User("tang", "唐僧", "wtfgvvdgdf", "18982455056", null, Enablement.PERMANENCE);
        CFO.assignUser(tang);
        userRepository.save(tang);
        roleRepository.save(CFO);
        //open box
        Resource box = new Resource("box", "錢箱", Zhu_Bajie.toCreator());
        resourceRepository.save(box);
        Processor open = new Processor(EngineManager.queryEngine("open_box"), Fuel.LUBRICANT);
        Permission permission = new Permission("6666", "打开钱箱", cashier.toRoleDescriptor(), open, box.toResourceDescriptor());
        permissionRepository.save(permission);
        permission = new Permission("6665", "打开钱箱", cashierSupr.toRoleDescriptor(), open, box.toResourceDescriptor());
        permissionRepository.save(permission);
        //refund
        Resource catalog = new Resource("catalog", "产品目录", Son_Goku.toCreator());
        resourceRepository.save(catalog);

        Processor refund = new Processor(EngineManager.queryEngine("refund"), Fuel.LUBRICANT);
        permission = new Permission("5555", "退货", cashierSupr.toRoleDescriptor(), refund, catalog.toResourceDescriptor());
        permissionRepository.save(permission);

        Resource fresh = new Resource("fresh", "生鲜", Son_Goku.toCreator());
        fresh.assignTo(catalog);
        resourceRepository.save(fresh);
        Resource meat = new Resource("meat", "鲜肉", Zhu_Bajie.toCreator());
        meat.assignTo(fresh);
        resourceRepository.save(meat);
        Resource Two_knife_meat = new Resource("Two_knife_meat", "二刀肉", Sand_Monk.toCreator());
        Two_knife_meat.assignTo(meat);
        resourceRepository.save(Two_knife_meat);

        Resource fruit = new Resource("fruit", "水果", Zhu_Bajie.toCreator());
        fruit.assignTo(fresh);
        resourceRepository.save(fruit);
        Resource apple = new Resource("apple", "苹果", Bailong.toCreator());
        apple.assignTo(fruit);
        resourceRepository.save(apple);
        Resource orange = new Resource("orange", "橘子", Bailong.toCreator());
        orange.assignTo(fruit);
        resourceRepository.save(orange);

        //discount
        Processor discount = new Processor(EngineManager.queryEngine("discount"), new Fuel("rate>=20"));
        Permission discountPermission = new Permission("7770", "discount", CFO.toRoleDescriptor(), discount, catalog.toResourceDescriptor());
        permissionRepository.save(discountPermission);
        discount = new Processor(EngineManager.queryEngine("discount"), new Fuel("rate>=30"));
        discountPermission = new Permission("7771", "discount", cashierSupr.toRoleDescriptor(), new Schedule("* 20 12 33"), discount, apple.toResourceDescriptor());
        permissionRepository.save(discountPermission);
        discountPermission = new Permission("7772", "discount", cashierSupr.toRoleDescriptor(), new Schedule("* 18 12 33"), discount, apple.toResourceDescriptor());
        permissionRepository.save(discountPermission);
        discount = new Processor(EngineManager.queryEngine("discount"), new Fuel("rate>=35"));
        discountPermission = new Permission("7773", "discount", cashierSupr.toRoleDescriptor(), discount, catalog.toResourceDescriptor());
        permissionRepository.save(discountPermission);
        discount = new Processor(EngineManager.queryEngine("discount"), new Fuel("rate>=40"));
        discountPermission = new Permission("7774", "discount", cashierSupr.toRoleDescriptor(), discount, meat.toResourceDescriptor());
        permissionRepository.save(discountPermission);
        discount = new Processor(EngineManager.queryEngine("discount"), new Fuel("rate>=45"));
        discountPermission = new Permission("7775", "discount", cashierSupr.toRoleDescriptor(), discount, fresh.toResourceDescriptor());
        permissionRepository.save(discountPermission);
        discount = new Processor(EngineManager.queryEngine("discount"), new Fuel("rate>=50"));
        discountPermission = new Permission("7776", "discount", cashier.toRoleDescriptor(), discount, fresh.toResourceDescriptor());
        permissionRepository.save(discountPermission);
        discount = new Processor(EngineManager.queryEngine("discount"), new Fuel("rate>=60"));
        discountPermission = new Permission("7777", "discount", cashier.toRoleDescriptor(), discount, Two_knife_meat.toResourceDescriptor());
        permissionRepository.save(discountPermission);

        Processor red = new Processor(EngineManager.queryEngine("red_catalog"), new Fuel("value<=45.00"));
        Permission redPermission = new Permission("8888", "red_catalog", cashier.toRoleDescriptor(), red, meat.toResourceDescriptor());
        permissionRepository.save(redPermission);
    }

    @AfterClass
    public static void teardown() {
        /*
        userRepository.remove("Bailong");
        userRepository.remove("Zhu_Bajie");
        userRepository.remove("Sand_Monk");
        userRepository.remove("Sun_WuKong");
        userRepository.remove("tang");

        resourceRepository.remove("box");
        resourceRepository.remove("orange");
        resourceRepository.remove("apple");
        resourceRepository.remove("fruit");
        resourceRepository.remove("Two_knife_meat");
        resourceRepository.remove("meat");
        resourceRepository.remove("fresh");
        resourceRepository.remove("catalog");

        roleRepository.remove("cashier");
        roleRepository.remove("cashierSupr");
        roleRepository.remove("CFO");
           */
    }

    @Test
    public void authorization() {
        Result result = authorizationService.authorization("Sun_WuKong", "打开钱箱", "box");
        Assert.assertEquals(result, Result.PERMIT);
        result = authorizationService.authorization("Sand_Monk", "打开钱箱", "box");
        Assert.assertEquals(result, Result.PERMIT);
        result = authorizationService.authorization("Bailong", "打开钱箱", "box");
        Assert.assertEquals(result, Result.FORBIDDEN);
        result = authorizationService.authorization("tang", "打开钱箱", "box");
        Assert.assertEquals(result, Result.FORBIDDEN);

        result = authorizationService.authorization("Zhu_Bajie", "退货", "catalog");
        Assert.assertEquals(result, Result.FORBIDDEN);
        result = authorizationService.authorization("Sun_WuKong", "退货", "catalog");
        Assert.assertEquals(result, Result.PERMIT);
    }

    @Test
    public void backtrackingCategoryauthorization() {
        VariantContext context = new VariantContext();
        context.put("rate", 20);
        context.put("categoryId","catalog");
        Result result = authorizationService.backtrackingCategoryauthorization("tang", "discount", "catalog", context);
        Assert.assertEquals(result, Result.PERMIT);
        context.put("rate", 19);
        context.put("categoryId","catalog");
        result = authorizationService.backtrackingCategoryauthorization("tang", "discount", "catalog", context);
        Assert.assertEquals(result.code(), ResultStatusCode.Forbidden);

        context.put("rate", 40);
        context.put("categoryId","fruit");
        result = authorizationService.backtrackingCategoryauthorization("Sun_WuKong", "discount", "orange", context);
        System.out.println(result);

        context.put("rate", 48);
        context.put("categoryId", "fruit");
        result = authorizationService.backtrackingCategoryauthorization("Sun_WuKong", "discount", "orange", context);
        System.out.println(result);
        result = authorizationService.backtrackingCategoryauthorization("Sun_WuKong", "discount", "banana", context);
        System.out.println(result);
    }
}