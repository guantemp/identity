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

package identity.hoprxi.core.application.command;

/**
 * @author <a href="www.hoprxi.com/author/guan xianghuang">guan xiangHuan</a>
 * @version 0.0.1 2020-12-19
 * @since JDK8.0
 */
public class SocializationBindUserCommand {
    private String username;
    private String unionId;
    private String thirdPartyName;

    public SocializationBindUserCommand(String username, String unionId, String thirdPartyName) {
        this.username = username;
        this.unionId = unionId;
        this.thirdPartyName = thirdPartyName;
    }

    public String getUsername() {
        return username;
    }

    public String getUnionId() {
        return unionId;
    }

    public String getThirdPartyName() {
        return thirdPartyName;
    }
}
