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

package identity.foxtail.core.domain.servers;

import org.junit.Test;

/***
 * @author <a href="www.foxtail.cc/authors/guan xiangHuan">guan xiangHuan</a>
 * @since JDK8.0
 * @version 0.0.1 2019-02-28
 */
public class PasswordServiceTest {

    @Test
    public void generateStrongPassword() {
        PasswordService ps = new PasswordService();
        System.out.println(ps.generateStrongPassword());
    }

    @Test
    public void isStrong() {
        PasswordService ps = new PasswordService();
        System.out.println(ps.isStrong("上海市"));
    }

    @Test
    public void isVeryStrong() {
    }

    @Test
    public void isWeak() {
    }
}