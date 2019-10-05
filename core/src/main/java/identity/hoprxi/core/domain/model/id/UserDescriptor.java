/*
 *  Copyright 2018 www.hoprxi.com All rights Reserved.
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
package identity.hoprxi.core.domain.model.id;

import java.time.LocalDateTime;

/***
 * @author <a href="www.hoprxi.com/authors/guan xiangHuan">guan xiangHuan</a>
 * @since JDK8.0
 * @version 0.0.1 20171228
 */
public class UserDescriptor {
    public static final UserDescriptor NullUserDescriptor = new UserDescriptor();
    private String username;
    private String id;
    private LocalDateTime expiryDateTime;

    /**
     * only for null user descriptor
     */
    private UserDescriptor() {
        this.id = "null";
        this.username = "null user";
        this.expiryDateTime = LocalDateTime.MAX;
    }

    /**
     * @param id
     * @param username
     * @param expiryDateTime
     */
    protected UserDescriptor(String id, String username, LocalDateTime expiryDateTime) {
        this.id = id;
        this.username = username;
        this.expiryDateTime = expiryDateTime;
    }

    public String username() {
        return username;
    }

    public String id() {
        return id;
    }

    public LocalDateTime expiryDateTime() {
        return expiryDateTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserDescriptor that = (UserDescriptor) o;

        return id != null ? id.equals(that.id) : that.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
