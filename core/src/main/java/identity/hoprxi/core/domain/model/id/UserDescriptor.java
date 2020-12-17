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

import java.util.StringJoiner;

/***
 * @author <a href="www.hoprxi.com/authors/guan xiangHuan">guan xiangHuan</a>
 * @since JDK8.0
 * @version 0.0.2 2020-12-17
 */
public class UserDescriptor {
    public static final UserDescriptor NullUserDescriptor = new UserDescriptor();
    private String username;
    private String id;
    private boolean available;

    /**
     * only for null user descriptor
     */
    private UserDescriptor() {
        this.id = "null";
        this.username = "null user";
        this.available = true;
    }

    /**
     * @param id
     * @param username
     * @param available
     */
    protected UserDescriptor(String id, String username, boolean available) {
        this.id = id;
        this.username = username;
        this.available = available;
    }

    public String username() {
        return username;
    }

    public String id() {
        return id;
    }

    public boolean isAvailable() {
        return available;
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

    @Override
    public String toString() {
        return new StringJoiner(", ", UserDescriptor.class.getSimpleName() + "[", "]")
                .add("username='" + username + "'")
                .add("id='" + id + "'")
                .add("available=" + available)
                .toString();
    }
}
