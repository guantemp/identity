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

import identity.hoprxi.core.domain.model.id.*;
import identity.hoprxi.core.infrastructure.persistence.ArangoDBSocializationRepository;
import identity.hoprxi.core.infrastructure.persistence.ArangoDBUserRepository;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;


/**
 * @author <a href="www.hoprxi.com/author/guan xianghuang">guan xiangHuan</a>
 * @version 0.0.1 2020-12-20
 * @since JDK8.0
 */
public class AuthenticationServiceTest {
    private static UserRepository repository = new ArangoDBUserRepository("identity");
    private static ArangoDBSocializationRepository socRepository = new ArangoDBSocializationRepository("identity");
    private static AuthenticationService service = new AuthenticationService(repository);
    private static UserSocializationService service1 = new UserSocializationService();
    private static String userId[] = new String[1];

    @AfterClass
    public static void teardown() {
        for (String id : userId)
            repository.remove(id);
        socRepository.remove("ojuOc5fgU_HH2PYklITXWmXfq620");
    }

    @BeforeClass
    public void setUpBeforeClass() {
        User user = new User(repository.nextIdentity(), "18982455055", "Qwe12rt7uyrt", null,
                "sdfsheas@126.com", Enablement.PERMANENCE);
        userId[0] = user.id();
        repository.save(user);
        service1.socializationBindUser("18982455055", "ojuOc5fgU_HH2PYklITXWmXfq620", "WECHAT");

    }

    @Test
    public void testAuthenticateByUsernameAndPassword() {
        UserDescriptor userDescriptor = service.authenticateByUsernameAndPassword("18982455055", "Qwe12rt7uyrt");
        Assert.assertEquals(userDescriptor.id(), userId[0]);
        userDescriptor = service.authenticateByUsernameAndPassword("18982455055", "Qwe2rt7uyrt");
        Assert.assertTrue(userDescriptor == UserDescriptor.NullUserDescriptor);
        userDescriptor = service.authenticateByUsernameAndPassword("18982455055", "Qwe12rt7uyrt");
    }

    @Test
    public void testAuthenticateByTelAndPassword() {
        UserDescriptor userDescriptor = service.authenticateByTelAndPassword("18982455055", "Qwe12rt7uyrt");
        Assert.assertEquals(userDescriptor.id(), userId[0]);
        userDescriptor = service.authenticateByTelAndPassword("18982455055", "Qwe2rt7urt");
        Assert.assertTrue(userDescriptor == UserDescriptor.NullUserDescriptor);
    }

    @Test
    public void testAuthenticateByTelAndSmsCode() {
    }

    @Test
    public void testAuthenticateByEmailAndPassword() {
        UserDescriptor userDescriptor = service.authenticateByEmailAndPassword("sdfsheas@126.com", "Qwe12rt7uyrt");
        Assert.assertEquals(userDescriptor.id(), userId[0]);
        userDescriptor = service.authenticateByEmailAndPassword("sdfsheas@126.com", "Qe12rt7uyrt");
        Assert.assertTrue(userDescriptor == UserDescriptor.NullUserDescriptor);
        userDescriptor = service.authenticateByEmailAndPassword("sdfshes@126.com", "Qwe12rt7uyrt");
        Assert.assertTrue(userDescriptor == UserDescriptor.NullUserDescriptor);
    }

    @Test
    public void testAuthenticateByThirdParty() {
        UserDescriptor userDescriptor = service.authenticateByThirdParty("ojuOc5fgU_HH2PYklITXWmXfq620");
        System.out.println(userDescriptor);
            /*
            Socialization socialization = socRepository.find("ojuOc5fgU_HH2PYklITXWmXfq620");
        Assert.assertNotNull(socialization);
        UserDescriptor userDescriptor = service.getSocializationBindUser("dy325fbg54");
        Assert.assertEquals(userDescriptor.id(), "tangtang");
        userDescriptor = service.getSocializationBindUser("4634567");
        Assert.assertTrue(userDescriptor == UserDescriptor.NullUserDescriptor);
        socialization = socRepository.find("dy325f1bg54");
        Assert.assertNull(socialization);
        userDescriptor = service.getSocializationBindUser("ojuOc5fgU_HH2PYklITXWmXfq620");
        Assert.assertEquals(userDescriptor.id(), "shasheng");
             */
    }
}