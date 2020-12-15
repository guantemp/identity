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


import identity.hoprxi.core.domain.model.id.Enablement;
import identity.hoprxi.core.domain.model.id.User;
import identity.hoprxi.core.domain.model.id.UserRepository;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.time.LocalDateTime;

/**
 * @author <a href="www.hoprxi.com/author/guan xianghuang">guan xiangHuan</a>
 * @version 0.0.1 2020-12-05
 * @since JDK8.0
 */
public class ArangoDBUserRepositoryTest {

    private static UserRepository repository = new ArangoDBUserRepository("identity");

    @AfterClass
    public static void teardown() {
        repository.remove("shifu");
        repository.remove("ershixiong");
        repository.remove("dashixiong");
        repository.remove(User.ANONYMOUS.id());
    }

    @BeforeClass
    public void setUpBeforeClass() throws Exception {
        User shifu = new User("shifu", "唐僧", "Qwe123465", "13679682333", new Enablement(true, LocalDateTime.now().plusDays(40)));
        repository.save(shifu);
        User dashixiong = new User("dashixiong", "孙悟空", "Qwe1234653", "18982455055", new Enablement(true, LocalDateTime.now().plusSeconds(30)));
        repository.save(dashixiong);
        User ershixiong = new User("ershixiong", "猪八戒", "Qwe1234655", "18982455066", new Enablement(true, LocalDateTime.now().plusMinutes(15)));
        repository.save(ershixiong);
        User shasheng = new User("shasheng", "沙僧", "Qwe12346535", "18982435170");
        repository.save(shasheng);
        User xiaobai = new User("bailongma", "白龙马", "Qwe12346535", "18982495270");
        repository.save(xiaobai);
        repository.save(User.ANONYMOUS);
    }

    @Test
    public void all() {
        User[] users = repository.all(0, 1);
        Assert.assertEquals(1, users.length);
        users = repository.all(0, 2);
        Assert.assertEquals(2, users.length);
        users = repository.all(2, 2);
        Assert.assertEquals(2, users.length);
        users = repository.all(5, 1);
        Assert.assertEquals(1, users.length);
    }

    @Test
    public void nextIdentity() throws Exception {
        Assert.assertNotNull(repository.nextIdentity());
    }

    @Test
    public void save() throws Exception {
        User chang = repository.find("dashixiong");
        chang.rename("看我七十二变->牛魔王");
        chang.changTelephoneNumber("17782455066");
        chang.changPassword("Qwe1234653", "guanQ24,.io23");
        chang.defineEnablement(Enablement.FOREVER);
        repository.save(chang);
        chang = repository.telephoneNumberAuthenticCredentials("17782455066", "guanQ24,.io23");
        Assert.assertEquals("17782455066", chang.telephoneNumber());
        Assert.assertTrue(chang.isEnable());
        Assert.assertEquals(chang.username(), "看我七十二变->牛魔王");
    }

    @Test
    public void find() throws Exception {
        User exists = repository.usernameAuthenticCredentials("唐僧", "Qwe123465");
        Assert.assertNotNull(exists);
        exists = repository.usernameAuthenticCredentials("唐僧", "qwe123465");
        Assert.assertNull(exists);
        User anonymous = repository.find("anonymous");
        Assert.assertSame(User.ANONYMOUS, anonymous);
        repository.remove("shasheng");
        User remove = repository.find("shasheng");
        Assert.assertNull(remove);
    }

    @Test
    public void isExists() throws Exception {
        Assert.assertTrue(repository.isUsernameExists("唐僧"));
        Assert.assertTrue(repository.isTelephoneNumberExists("18982455055"));
        Assert.assertTrue(repository.isUsernameExists("白龙马"));
        Assert.assertTrue(repository.isTelephoneNumberExists("18982495270"));
        repository.remove("bailongma");
        Assert.assertFalse(repository.isUsernameExists("白龙马"));
        Assert.assertFalse(repository.isTelephoneNumberExists("18982495270"));
    }
}