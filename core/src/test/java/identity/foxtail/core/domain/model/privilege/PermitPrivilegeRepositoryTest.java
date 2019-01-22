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

package identity.foxtail.core.domain.model.privilege;

import identity.foxtail.core.domain.model.element.Resource;
import identity.foxtail.core.domain.model.element.ResourceRepository;
import identity.foxtail.core.domain.model.element.Role;
import identity.foxtail.core.domain.model.element.RoleRepository;
import identity.foxtail.core.domain.model.id.*;
import identity.foxtail.core.infrastructure.persistence.*;
import org.junit.BeforeClass;
import org.junit.Test;

/***
 * @author <a href="www.foxtail.cc/authors/guan xiangHuan">guan xiangHuan</a>
 * @since JDK8.0
 * @version 0.0.1 2019-01-22
 */
public class PermitPrivilegeRepositoryTest {
    private static final RoleRepository roleRepository = new ArangoDBRoleRepository();
    private static final GroupRepository groupRepository = new ArangoDBGroupRepository();
    private static final UserRepository userRepository = new ArangoDBUserRepository();
    private static final GroupMemberService service = new GroupMemberService(groupRepository);
    private static final ResourceRepository resourceRepository = new ArangoDBResourceRepository();
    private static final PermitPrivilegeRepository repo = new ArangoDBPermitPrivilegeRepository();

    @BeforeClass
    public static void setUpBeforeClass() {
        Role casher = new Role("casher", "收銀員", "就是收钱的");
        User user = new User("Son_Goku", "孫悟空", "中文密碼也是可以的", "0830-2135679", Enablement.FOREVER);
        userRepository.save(user);
        casher.assignUser(user);
        roleRepository.save(casher);

        Resource box = new Resource("box", "錢箱", user.toCreator());
        resourceRepository.save(box);
        Job job = new Job("open", "OPEN_BOX_AQL");
        PermitPrivilege privilege = new PermitPrivilege("676767", casher.toRoleDescriptor(), job, box.toResourceDescriptor());
        repo.save(privilege);

        Resource catalog = new Resource("catalog", "产品目录", user.toCreator());
        resourceRepository.save(catalog);
        Resource fresh = new Resource("fresh", "生鲜", user.toCreator());
        fresh.assignTo(catalog);
        resourceRepository.save(fresh);
    }

    @Test
    public void isPermited() {
    }
}