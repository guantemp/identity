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

package identity.hoprxi.core.domain.servers;

import org.junit.Assert;
import org.junit.Test;

/***
 * @author <a href="www.hoprxi.com/authors/guan xiangHuan">guan xiangHuan</a>
 * @since JDK8.0
 * @version 0.0.1 2019-02-28
 */
public class PasswordServiceTest {

    @Test
    public void generateStrongPassword() {
        PasswordService ps = new PasswordService();
        Assert.assertTrue(ps.isStrong(ps.generateStrongPassword()));
        System.out.println(ps.generateStrongPassword());
        System.out.println(ps.generateStrongPassword());
        System.out.println(ps.generateStrongPassword());
    }

    @Test
    public void strong() {
        PasswordService ps = new PasswordService();
        Assert.assertTrue(ps.isStrong("32534sdgd12"));
        Assert.assertTrue(ps.isWeak("wqrewr"));
        Assert.assertTrue(ps.isVeryStrong("#fcsd2yd54nb65"));
    }
}