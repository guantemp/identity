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

import java.util.Iterator;

/***
 * @author <a href="www.foxtail.cc/authors/guan xiangHuan">guan xiangHuan</a>
 * @since JDK8.0
 * @version 0.0.2 20180108
 */
public class GroupMemberService {
    private GroupRepository groupRepository;

    public GroupMemberService(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }

    /**
     * @param group
     * @param member
     * @return true if member of group
     */
    public boolean isMemberInGroup(Group group, GroupMember member) {
        //self is true
        if (group.toGroupMember().equals(member))
            return true;
        boolean isIn = false;
        Iterator<GroupMember> iterator = group.members().iterator();
        while (!isIn && iterator.hasNext()) {
            GroupMember gm = iterator.next();
            if (gm.isUser() && member.isUser() && gm.equals(member)) {
                return true;
            } else if (gm.isGroup()) {
                if (member.isGroup() && gm.equals(member)) {
                    return true;
                } else {
                    group = groupRepository.find(gm.userOrGroupId());
                    if (group != null)
                        isIn = isMemberInGroup(group, member);
                }
            }
        }
        return isIn;
    }

    /**
     * @param group
     * @param nestedGroup
     * @return
     */
    public boolean isGroupInGroup(Group group, Group nestedGroup) {
        if (group == nestedGroup)
            return true;
        return isMemberInGroup(group, nestedGroup.toGroupMember());
    }
}
