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

import org.testng.annotations.Test;

/**
 * @author <a href="www.hoprxi.com/author/guan xianghuang">guan xiangHuan</a>
 * @version 0.0.1 2020-12-18
 * @since JDK8.0
 */
public class UserSocializationServiceTest {
    private UserSocializationService service = new UserSocializationService();

    @Test(priority = 1)
    public void testRegisterUser() {
        service.registerUser("13679692333", "sgjk325m,few", null,
                "34354mdms@ewr.org", Enablement.PERMANENCE.isEnable(), Enablement.PERMANENCE.deadline());
        service.registerUser("13679692331", null, null,
                null, Enablement.PERMANENCE.isEnable(), Enablement.PERMANENCE.deadline());
        service.registerUser("13679692332", null, null,
                null, Enablement.PERMANENCE.isEnable(), Enablement.PERMANENCE.deadline());
        service.registerUser("guant@126.com", null, null,
                null, Enablement.PERMANENCE.isEnable(), Enablement.PERMANENCE.deadline());
        service.registerUser("5227854@qq.com", null, null,
                null, Enablement.PERMANENCE.isEnable(), Enablement.PERMANENCE.deadline());
    }

    @Test(priority = 2)
    public void testBindUser() {
        service.bindUser("52237854@qq.com", "ojuOc5fgU_HH2PYklITXWmXfq620", "WECHAT");
        service.bindUser("32788@qq.com", "UvxXLQoYmArlxnBqmbpaA==", "WECHAT");
    }

    @Test
    public void testUnbindUser() {
    }

    @Test
    public void testGetBindUser() {
        UserDescriptor userDescriptor = service.getBindUser("ojuOc5fgU_HH2PYklITXWmXfq620");
        System.out.println(userDescriptor);
    }
}