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
import identity.hoprxi.core.domain.model.id.UserSocializationService;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;

/**
 * @author <a href="www.hoprxi.com/author/guan xianghuang">guan xiangHuan</a>
 * @version 0.0.1 2020-12-15
 * @since JDK8.0
 */
public class ArangoDBSocializationRepositoryTest {
    private static UserRepository repository = new ArangoDBUserRepository("identity");
    private static ArangoDBSocializationRepository socRepository = new ArangoDBSocializationRepository("identity");
    private static UserSocializationService service = new UserSocializationService();

    @AfterClass
    public static void teardown() {
        repository.remove("tangtang");
        repository.remove("shasheng");
        socRepository.remove("dy325fbg54");
        socRepository.remove("ojuOc5fgU_HH2PYklITXWmXfq620");
        socRepository.remove("sdtyger345634");
    }

    @BeforeClass
    public void setUpBeforeClass() {
        User lili = new User("tangtang", "唐僧", "Qwe123465", "13679682301", null, new Enablement(true, LocalDateTime.now().plusDays(40)));
        repository.save(lili);
        User shasheng = new User("shasheng", "沙僧", "Qwe12346535", "18982435170", null, Enablement.PERMANENCE);
        repository.save(shasheng);
    }

    @Test(priority = 2)
    public void testSave() throws IllegalAccessException, InvocationTargetException, InstantiationException {
        service.socializationBindUser("唐僧", "dy325fbg54", "WECHAT");
        service.socializationBindUser("沙僧", "ojuOc5fgU_HH2PYklITXWmXfq620", "WECHAT");
        service.socializationBindUser("唐僧", "sdtyger345634", "QQ");
        service.socializationBindUser("沙僧", "f1f1f1f1", "WECHAT");
        service.socializationUnbindUser("f1f1f1f1");
    }
}