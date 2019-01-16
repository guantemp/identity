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
package identity.foxtail.domain.model.id;

import java.util.Objects;

/***
 * @author <a href="www.foxtail.cc/authors/guan xiangHuan">guan xiangHuan</a>
 * @since JDK8.0
 * @version 0.0.2 2017-12-26
 */
public class GroupMember {
    private GroupMemberType type;
    private String userOrGroupId;

    /**
     * @param type
     * @param userOrGroupId
     * @throws IllegalAccessException if type is <code>NULL</code> or userOrGroupId non conformity
     */
    protected GroupMember(GroupMemberType type, String userOrGroupId) {
        this.type = Objects.requireNonNull(type, "Type is required");
        this.userOrGroupId = userOrGroupId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GroupMember that = (GroupMember) o;

        if (type != that.type) return false;
        return userOrGroupId != null ? userOrGroupId.equals(that.userOrGroupId) : that.userOrGroupId == null;
    }

    @Override
    public int hashCode() {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + (userOrGroupId != null ? userOrGroupId.hashCode() : 0);
        return result;
    }

    public boolean isGroup() {
        return type.isGroup();
    }

    public boolean isUser() {
        return type.isUser();
    }

    @Override
    public String toString() {
        return "GroupMember{" +
                "type=" + type +
                ", userOrGroupId='" + userOrGroupId + '\'' +
                '}';
    }

    public GroupMemberType type() {
        return type;
    }

    public String userOrGroupId() {
        return userOrGroupId;
    }
}
