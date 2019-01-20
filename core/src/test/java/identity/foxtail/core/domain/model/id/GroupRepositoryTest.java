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
package identity.foxtail.core.domain.model.id;

import identity.foxtail.core.infrastructure.persistence.ArangoDBGroupRepository;
import identity.foxtail.core.infrastructure.persistence.ArangoDBUserRepository;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.time.LocalDateTime;

/***
 * @author <a href="www.foxtail.cc/author/guan xiangHuan">guan xiangHuan</a>
 * @since JDK8.0
 * @version 0.0.1 20180102
 */
public class GroupRepositoryTest {
    private static GroupRepository groupRepository = new ArangoDBGroupRepository();
    private static UserRepository userRepository = new ArangoDBUserRepository();
    private static GroupMemberService service = new GroupMemberService(groupRepository);

    @BeforeClass
    public static void setUpBeforeClass() {
        Group cashier = new Group("cashier", "收银员_A组", "被限制权力的收银员");
        User u1 = new User("chuxianzi", "楚仙子", "32545eter75", "0833-2175215", new Enablement(true, LocalDateTime.now().plusMinutes(5)));
        User u2 = new User("wanghao", "王浩", "436843idsio", "0830-2117535", new Enablement(true, LocalDateTime.now().plusDays(3)));
        userRepository.save(u1);
        userRepository.save(u2);
        cashier.addUser(u1);
        cashier.addUser(u2);
        groupRepository.save(cashier);

        Group director_of_the_cashier = new Group("director_of_the_cashier", "收银主管", "前台收银管理全部权限");
        User u3 = new User("13679692301", "观自在", "asd79692301", "13679692301", new Enablement(true, LocalDateTime.now().plusDays(30)));
        userRepository.save(u3);
        director_of_the_cashier.addUser(u3);
        groupRepository.save(director_of_the_cashier);

        Group HongXing_2 = new Group("HongXing_2", "红星二店");
        groupRepository.save(HongXing_2);
        Group HongXing_1 = new Group("HongXing_1", "红星一店", "龙马潭区红星村农贸市场");
        groupRepository.save(HongXing_1);
        Group cashier_Group = new Group("cashier_Group", "收银员组", "收银员组的根");
        cashier_Group.addGroup(director_of_the_cashier, service);
        cashier_Group.addGroup(cashier, service);
        cashier_Group.addGroup(HongXing_2, service);
        cashier_Group.addGroup(HongXing_1, service);
        groupRepository.save(cashier_Group);
        Group shopowner = new Group("shopowner", "Hongxin_1多个父组", "Hongxin_1被加到（shopowner)下面");
        shopowner.addGroup(HongXing_1, service);
        groupRepository.save(shopowner);
        cashier_Group.addGroup(shopowner, service);
        groupRepository.save(cashier_Group);
    }


    @AfterClass
    public static void tearDown() throws Exception {
        userRepository.remove("chuxianzi");
        userRepository.remove("wanghao");
        userRepository.remove("13679692301");
        groupRepository.remove("cashier");
        groupRepository.remove("director_of_the_cashier");
        groupRepository.remove("HongXing_1");
        groupRepository.remove("shopowner");
        groupRepository.remove("cashier_Group");
    }

    @Test
    public void find() {
        Group cashier_Group = groupRepository.find("cashier_Group");
        Assert.assertNotNull(cashier_Group);
        Group shopowner = groupRepository.find("shopowner");
        Assert.assertNotNull(shopowner);

        User aUser = userRepository.find("13679692301");
        userRepository.find("chuxianzi");
        User bUser = new User("root", "root", "Qwe123465", "0431-4567890", Enablement.FOREVER);
        Assert.assertTrue(cashier_Group.isUserInGroup(aUser, service));
        Assert.assertFalse(cashier_Group.isUserInGroup(bUser, service));

        Group[] groups = groupRepository.all(0, 7);
        Assert.assertEquals(5, groups.length);
    }

    @Test
    public void remove() {
        Group HongXing_1 = groupRepository.find("HongXing_1");
        Assert.assertNotNull(HongXing_1);
        Group HongXing_2 = groupRepository.find("HongXing_2");
        Assert.assertNotNull(HongXing_2);
        //remove find parent
        Group cashier_Group = groupRepository.find("cashier_Group");
        cashier_Group.removeGroup(HongXing_1);
        cashier_Group.removeGroup(HongXing_2);
        groupRepository.save(cashier_Group);
        //Can't delete the shopowner because it's has child HongXing_1
        groupRepository.remove("shopowner");
        Group shopowner = groupRepository.find("shopowner");
        Assert.assertNotNull(shopowner);
        //remove
        groupRepository.remove("HongXing_2");
        HongXing_2 = groupRepository.find("HongXing_2");
        Assert.assertNull(HongXing_2);
    }
}