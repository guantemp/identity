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

package identity.foxtail.core.domain.servers;

import identity.foxtail.core.domain.model.element.Resource;
import identity.foxtail.core.domain.model.element.ResourceRepository;
import identity.foxtail.core.domain.model.element.Role;
import identity.foxtail.core.domain.model.element.RoleRepository;
import identity.foxtail.core.domain.model.id.*;
import identity.foxtail.core.domain.model.permission.*;
import identity.foxtail.core.infrastructure.persistence.*;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/***
 * @author <a href="www.foxtail.cc/authors/guan xiangHuan">guan xiangHuan</a>
 * @since JDK8.0
 * @version 0.0.1 2019-03-04
 */
public class AuthorizationServiceTest {
    private static final RoleRepository roleRepository = new ArangoDBRoleRepository();
    private static final GroupRepository groupRepository = new ArangoDBGroupRepository();
    private static final UserRepository userRepository = new ArangoDBUserRepository();
    private static final GroupMemberService service = new GroupMemberService(groupRepository);
    private static final ResourceRepository resourceRepository = new ArangoDBResourceRepository();
    private static final PermissionRepository repo = new ArangoDBPermissionRepository();
    private static final AuthorizationService authorizationService = new AuthorizationService(userRepository, repo, roleRepository, service);

    @BeforeClass
    public static void setUpBeforeClass() {
        User Bailong = new User("Bailong", "白龙马", "@ewt56gh", "18982455058", Enablement.FOREVER);
        userRepository.save(Bailong);

        Role cashier = new Role("cashier", "收银员", "就是收钱的");
        User Sand_Monk = new User("Sand_Monk", "沙僧", ")dgdf324", "18982455057", Enablement.FOREVER);
        userRepository.save(Sand_Monk);
        cashier.assignUser(Sand_Monk);
        roleRepository.save(cashier);
        User Zhu_Bajie = new User("Zhu_Bajie", "猪八戒", "Wfhf456", "13679692301", Enablement.FOREVER);
        cashier.assignUser(Zhu_Bajie);
        userRepository.save(Zhu_Bajie);
        roleRepository.save(cashier);

        Role cashierSupr = new Role("cashierSupr", "收银主管", "收银员的老大");
        User Son_Goku = new User("Sun_WuKong", "孫悟空", "%dfhdfs26", "0830-2135679", Enablement.FOREVER);
        cashierSupr.assignUser(Son_Goku);
        userRepository.save(Son_Goku);
        roleRepository.save(cashierSupr);

        Role CFO = new Role("CFO", "财务总监", "财政一把手，对董事会负责");
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
        permission = new Permission("6665", "打开钱箱", cashierSupr.toRoleDescriptor(), open, box.toResourceDescriptor());
        repo.save(permission);
        //refund
        Resource catalog = new Resource("catalog", "产品目录", Son_Goku.toCreator());
        resourceRepository.save(catalog);

        Processor refund = new Processor(EngineManager.queryEngine("refund"), Fuel.LUBRICANT);
        permission = new Permission("5555", "退货", cashierSupr.toRoleDescriptor(), refund, catalog.toResourceDescriptor());
        repo.save(permission);

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
        repo.save(discountPermission);

        discount = new Processor(EngineManager.queryEngine("discount"), new Fuel("rate>=30"));
        discountPermission = new Permission("7771", "discount", cashierSupr.toRoleDescriptor(), new Schedule("* 15 12 33"), discount, apple.toResourceDescriptor());
        repo.save(discountPermission);
        discountPermission = new Permission("7772", "discount", cashierSupr.toRoleDescriptor(), new Schedule("* 18 12 33"), discount, apple.toResourceDescriptor());
        repo.save(discountPermission);
        discount = new Processor(EngineManager.queryEngine("discount"), new Fuel("rate>=35"));
        discountPermission = new Permission("7777", "discount", cashierSupr.toRoleDescriptor(), discount, catalog.toResourceDescriptor());
        repo.save(discountPermission);
        discount = new Processor(EngineManager.queryEngine("discount"), new Fuel("rate>=40"));
        discountPermission = new Permission("7773", "discount", cashierSupr.toRoleDescriptor(), discount, meat.toResourceDescriptor());
        repo.save(discountPermission);
        discount = new Processor(EngineManager.queryEngine("discount"), new Fuel("rate>=45"));
        discountPermission = new Permission("7774", "discount", cashierSupr.toRoleDescriptor(), discount, fresh.toResourceDescriptor());
        repo.save(discountPermission);
        discount = new Processor(EngineManager.queryEngine("discount"), new Fuel("rate>=60"));
        discountPermission = new Permission("7775", "discount", cashier.toRoleDescriptor(), discount, Two_knife_meat.toResourceDescriptor());
        repo.save(discountPermission);
        discount = new Processor(EngineManager.queryEngine("discount"), new Fuel("rate>=50"));
        discountPermission = new Permission("7776", "discount", cashier.toRoleDescriptor(), discount, fresh.toResourceDescriptor());
        repo.save(discountPermission);

        Processor red = new Processor(EngineManager.queryEngine("red_catalog"), new Fuel("value<=45.00"));
        Permission redPermission = new Permission("8888", "red_catalog", cashier.toRoleDescriptor(), red, meat.toResourceDescriptor());
        repo.save(redPermission);
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
        Result result = authorizationService.backtrackingCategoryauthorization("tang", "discount", "catalog", "catalog", context);
        Assert.assertEquals(result, Result.PERMIT);
        context.clear();
        context.put("rate", 19);
        result = authorizationService.backtrackingCategoryauthorization("tang", "discount", "catalog", "catalog", context);
        Assert.assertEquals(result.code(), ResultStatusCode.Forbidden);

        result = authorizationService.backtrackingCategoryauthorization("Sun_WuKong", "discount", "orange", "catalog", context);
        System.out.println(result);
        result = authorizationService.backtrackingCategoryauthorization("Sun_WuKong", "discount", "banana", "catalog", context);
        System.out.println(result);
    }
}