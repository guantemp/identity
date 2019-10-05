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

/***
 * @author <a href="www.hoprxi.com/authors/guan XiangHuan">guan xiangHuan</a>
 * @since JDK8.0
 * @version 0.0.3 2018-11-13
 */
public interface UserRepository {
    /**
     * @return
     */
    long count();

    /**
     * @param offset
     * @param limit
     * @return
     */
    User[] all(long offset, int limit);

    /**
     * @return
     */
    String nextIdentity();

    /**
     * @param id
     */
    void remove(String id);

    /**
     * @param user
     */
    void save(User user);

    /**
     * @param id
     * @return
     */
    User find(String id);

    /**
     * @param username
     * @return
     */
    boolean isUsernameExists(String username);

    /**
     * @param username
     * @param password
     * @return
     */
    User usernameAuthenticCredentials(String username, String password);

    /**
     * @param telephoneNumber
     * @param password
     * @return
     */
    User telephoneNumberAuthenticCredentials(String telephoneNumber, String password);

    /**
     * @param telephoneNumber
     * @return
     */
    boolean isTelephoneNumberExists(String telephoneNumber);
}
