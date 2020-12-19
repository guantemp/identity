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

package identity.hoprxi.core.domain.model.id;

import identity.hoprxi.core.infrastructure.persistence.ArangoDBSocializationRepository;
import identity.hoprxi.core.infrastructure.persistence.ArangoDBUserRepository;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

/**
 * @author <a href="www.hoprxi.com/author/guan xianghuang">guan xiangHuan</a>
 * @version 0.0.1 2020-12-18
 * @since JDK8.0
 */
public class UserSocializationServiceTest {
    private static UserRepository repository = new ArangoDBUserRepository("identity");
    private static ArangoDBSocializationRepository socRepository = new ArangoDBSocializationRepository("identity");
    private UserSocializationService service = new UserSocializationService();
    private static String userId[] = new String[7];

    @AfterClass
    public static void teardown() {
        for (String id : userId)
            repository.remove(id);
        socRepository.remove("ojuOc5fgU_HH2PYklITXWmXfq620");
    }

    @Test(priority = 1)
    public void testRegisterUser() {
        UserDescriptor userDescriptor = service.registerUser("13679692333", "sgjk325m,few", null,
                "34354mdms@ewr.org", Enablement.PERMANENCE.isEnable(), Enablement.PERMANENCE.deadline());
        userId[0] = userDescriptor.id();
        userDescriptor = service.registerUser("13679692331", null, "13679692330",
                null, Enablement.PERMANENCE.isEnable(), Enablement.PERMANENCE.deadline());
        userId[1] = userDescriptor.id();
        userDescriptor = service.registerUser("13679692332", null, null,
                null, Enablement.PERMANENCE.isEnable(), Enablement.PERMANENCE.deadline());
        userId[2] = userDescriptor.id();
        userDescriptor = service.registerUser("guant@126.com", null, null,
                null, Enablement.PERMANENCE.isEnable(), Enablement.PERMANENCE.deadline());
        userId[3] = userDescriptor.id();
        userDescriptor = service.registerUser("5227854@qq.com", null, "13679692330",
                null, Enablement.PERMANENCE.isEnable(), Enablement.PERMANENCE.deadline());
        userId[4] = userDescriptor.id();
    }

    @Test(priority = 2)
    public void testBindUser() {
        UserDescriptor userDescriptor = service.socializationBindUser("52237854@qq.com", "ojuOc5fgU_HH2PYklITXWmXfq620", "WECHAT");
        userId[5] = userDescriptor.id();
        userDescriptor = service.socializationBindUser("32788@qq.com", "UvxXLQoYmArlxnBqmbpaA==", "WECHAT");
        userId[6] = userDescriptor.id();
    }

    @Test(priority = 3)
    public void testUnbindUser() {
        service.socializationUnbindUser("UvxXLQoYmArlxnBqmbpaA==");
    }

    @Test(priority = 3)
    public void testGetBindUser() {
        UserDescriptor userDescriptor = service.getSocializationBindUser("ojuOc5fgU_HH2PYklITXWmXfq620");
        System.out.println(userDescriptor);
    }
}