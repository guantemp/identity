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
package identity.hoprxi.core.domain.model.element;

import identity.hoprxi.core.domain.model.id.*;
import identity.hoprxi.core.infrastructure.persistence.ArangoDBGroupRepository;
import identity.hoprxi.core.infrastructure.persistence.ArangoDBRoleRepository;
import identity.hoprxi.core.infrastructure.persistence.ArangoDBUserRepository;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.time.LocalDateTime;

/***
 * @author <a href="www.hoprxi.com/author/guan xiangHuan">guan xiangHuan</a>
 * @since JDK8.0
 * @version 0.0.1 20180124
 */
public class RoleRepositoryTest {
    private static final RoleRepository roleRepository = new ArangoDBRoleRepository();
    private static final GroupRepository groupRepository = new ArangoDBGroupRepository();
    private static final UserRepository userRepository = new ArangoDBUserRepository("identity");
    private static final GroupMemberService service = new GroupMemberService(groupRepository);

    @BeforeClass
    public static void setUpBeforeClass() {
        Group finance_and_accounting_department = new Group("finance_and_accounting_department", "财会科", "管理收银员");

        User a = new User("laoda_2", "如来", "ewtredsf", "13679692308", new Enablement(true, LocalDateTime.now().plusDays(30)));
        userRepository.save(a);
        finance_and_accounting_department.addUser(a);
        groupRepository.save(finance_and_accounting_department);

        Group cashier_leader = new Group("cashier_leader", "收银组长");
        User c = new User("shifu", "唐僧", "Etrtwq676565", "13679692302", new Enablement(true, LocalDateTime.now().plusDays(30)));
        userRepository.save(c);
        cashier_leader.addUser(c);
        groupRepository.save(cashier_leader);

        Group cashier = new Group("cashier", "收银员组", "受限的收银员组别");
        User d = new User("da", "孙悟空", "Etrtwq676565", "13679692303", new Enablement(true, LocalDateTime.now().plusDays(30)));
        userRepository.save(d);
        User e = new User("er", "猪八戒", "Etrtwq6565", "13679692304", new Enablement(true, LocalDateTime.now().plusDays(30)));
        userRepository.save(e);
        cashier.addUser(d);
        cashier.addUser(e);
        cashier.addGroup(cashier_leader, service);
        groupRepository.save(cashier);


        Role cfo = new Role("CFO", "财务总监", "管理公司所有钱有关的事");
        cfo.assignGroup(finance_and_accounting_department, service);
        User b = new User("laoda_1", "观音", "ewtr35edsf", "13679692309", new Enablement(true, LocalDateTime.now().plusDays(30)));
        userRepository.save(b);
        cfo.assignUser(b);
        roleRepository.save(cfo);

        Role manager_of_cashier = new Role("manager_of_cashier", "收银主管", "前台收银管理");
        manager_of_cashier.assignGroup(cashier_leader, service);
        roleRepository.save(manager_of_cashier);

        Role role_cashier = new Role("cashier", "收银员", "前台收银部分权力");
        role_cashier.assignGroup(cashier, service);
        roleRepository.save(role_cashier);
    }
/*
    @AfterClass
    public static void terndown() {
        roleRepository.remove("CFO");
        roleRepository.remove("manager_of_cashier");
        roleRepository.remove("cashier");

        userRepository.remove("er");
        userRepository.remove("da");
        userRepository.remove("shifu");
        userRepository.remove("laoda_1");
        userRepository.remove("laoda_2");

        groupRepository.remove("cashier");
        groupRepository.remove("cashier_leader");
        groupRepository.remove("finance_and_accounting_department");
    }
    */

    @Test
    public void find() {
        Assert.assertEquals(3, roleRepository.count());
        Role cfo = roleRepository.find("CFO");
        Assert.assertNotNull(cfo);
        cfo = roleRepository.find("cFO");
        Assert.assertNull(cfo);
        Role[] roles = roleRepository.all(0, 3);
        Assert.assertEquals(roles.length, 3);
        roles = roleRepository.all(1, 3);
        Assert.assertEquals(roles.length, 2);
    }


    @Test
    public void isInRole() {
        Role cfo = roleRepository.find("CFO");
        Role cashier = roleRepository.find("cashier");
        Role manager_of_cashier = roleRepository.find("manager_of_cashier");

        User a = new ArangoDBUserRepository("identity").find("laoda_1");
        User b = new ArangoDBUserRepository("identity").find("shifu");
        User c = new ArangoDBUserRepository("identity").find("da");

        Assert.assertTrue(cfo.isUserInRole(a, service));
        Assert.assertFalse(cfo.isUserInRole(b, service));
        Assert.assertTrue(manager_of_cashier.isUserInRole(b, service));
        Assert.assertTrue(cashier.isUserInRole(b, service));
        Assert.assertTrue(cashier.isUserInRole(c, service));
        Assert.assertFalse(manager_of_cashier.isUserInRole(c, service));
    }
}