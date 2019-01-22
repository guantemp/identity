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

import com.arangodb.entity.DocumentField;
import com.arangodb.velocypack.annotations.Expose;
import identity.foxtail.core.domain.model.element.ResourceDescriptor;
import identity.foxtail.core.domain.model.element.RoleDescriptor;

/***
 * @author <a href="www.foxtail.cc/authors/guan xiangHuan">guan xiangHuan</a>
 * @since JDK8.0
 * @version 0.0.1 2019-01-21
 */
public class PermitPrivilege {
    @DocumentField(DocumentField.Type.KEY)
    private String id;
    @Expose(serialize = false, deserialize = false)
    private ResourceDescriptor resourceDescriptor;
    private Job job;
    private Schedule schedule;
    @Expose(serialize = false, deserialize = false)
    private RoleDescriptor roleDescriptor;

    public PermitPrivilege(String id, RoleDescriptor roleDescriptor, Job job, ResourceDescriptor resourceDescriptor) {
        this.id = id;
        this.roleDescriptor = roleDescriptor;
        this.job = job;
        this.resourceDescriptor = resourceDescriptor;
        this.schedule = schedule;
    }

    public static PermitPrivilege cretaeSchdulePermitPrivilege() {
        return null;
    }

    public boolean isPermited(JobContext jobContext) {
        return true;
    }

    public String id() {
        return id;
    }

    public ResourceDescriptor resourceDescriptor() {
        return resourceDescriptor;
    }

    public Job job() {
        return job;
    }

    public Schedule schedule() {
        return schedule;
    }

    public RoleDescriptor roleDescriptor() {
        return roleDescriptor;
    }
}
