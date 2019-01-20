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

package identity.foxtail.core.domain.model.power;

import identity.foxtail.core.domain.model.element.Resource;
import identity.foxtail.core.domain.model.element.ResourceRepository;
import identity.foxtail.core.domain.model.element.Role;
import identity.foxtail.core.domain.model.element.RoleRepository;
import identity.foxtail.core.domain.model.id.GroupMemberService;
import identity.foxtail.core.domain.model.id.GroupRepository;
import identity.foxtail.core.domain.model.id.User;
import identity.foxtail.core.domain.model.id.UserRepository;
import identity.foxtail.core.domain.model.job.Command;
import identity.foxtail.core.infrastructure.persistence.ArangoDBGroupRepository;
import identity.foxtail.core.infrastructure.persistence.ArangoDBResourceRepository;
import identity.foxtail.core.infrastructure.persistence.ArangoDBRoleRepository;
import identity.foxtail.core.infrastructure.persistence.ArangoDBUserRepository;
import org.junit.BeforeClass;
import org.junit.Test;

/***
 * @author <a href="www.foxtail.cc/authors/guan xiangHuan">guan xiangHuan</a>
 * @since JDK8.0
 * @version 0.0.1 2018-12-03
 */
public class PowerRepositoryTest {
    private static final RoleRepository roleRepository = new ArangoDBRoleRepository();
    private static final GroupRepository groupRepository = new ArangoDBGroupRepository();
    private static final UserRepository userRepository = new ArangoDBUserRepository();
    private static final GroupMemberService service = new GroupMemberService(groupRepository);
    private static final ResourceRepository resourceRepository = new ArangoDBResourceRepository();
    private static final PowerRepository powerRepository = new ArangoDBPowerRepository();

    @BeforeClass
    public static void setUpBeforeClass() {
        Role casher = new Role("casher", "收銀員", "收錢的");
        User user = new User("Son_Goku", "孫悟空", "中文密碼是可以的", "0830-2135679");
        userRepository.save(user);
        casher.assignUser(user);
        roleRepository.save(casher);

        Resource box = new Resource("676767", "錢箱", user.toCreator());
        resourceRepository.save(box);

        Command open = new Open();
        Power power = new Power("openbox", "打开", casher.toRoleDescriptor(), open, box.toResourceDescriptor(), null);
        powerRepository.save(power);
    }

    /*
        @AfterClass
        public static void teardown() {
           powerRepository.remove("openbox");
           userRepository.remove("Son_Goku");
           roleRepository.remove("casher");
           resourceRepository.remove("676767");
        }
    */
    @Test
    public void save() {
    }

    @Test
    public void find() {
    }

    @Test
    public void remove() {
    }

    public static class Open implements Command {
    }
}