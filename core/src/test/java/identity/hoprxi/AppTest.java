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

package identity.hoprxi;

import identity.hoprxi.core.domain.model.id.Enablement;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * @author <a href="www.hoprxi.com/author/guan xianghuang">guan xiangHuan</a>
 * @version 0.0.1 2020-12-17
 * @since JDK8.0
 */
public class AppTest {

    @Test
    public void test() {
        String str1 = null;
        boolean tf = Optional.ofNullable(str1).map(u -> str1.length() > 1 || str1.length() <= 255).orElse(false);
        Assert.assertFalse(tf);
        String str2 = "sdgftusd";
        Assert.assertTrue(Optional.ofNullable(str2).map(u -> str2.length() > 6 && str2.length() <= 255).orElse(false));
        String str3 = "sdd";
        Assert.assertFalse(Optional.ofNullable(str3).map(u -> str3.length() > 6 && str3.length() <= 255).orElse(false));
        Pattern EMAIL_PATTERN = Pattern.compile("^\\s*\\w+(?:\\.{0,1}[\\w-]+)*@[a-zA-Z0-9]+(?:[-.][a-zA-Z0-9]+)*\\.[a-zA-Z]+\\s*$");
        System.out.println(EMAIL_PATTERN.matcher("327885095@qq.com").matches());
        Enablement enablement = new Enablement(true, LocalDateTime.now().plusMinutes(15));
        System.out.println(Optional.ofNullable(enablement).map(e -> enablement).orElse(Enablement.PERMANENCE));

        String user = null;
        System.out.println("user:");
        System.out.println(Optional.ofNullable(user).map(u -> user.length() > 1 && user.length() <= 255).orElse(false));

        String telephoneNumber = null;
        String username = "13679692308";
        Pattern MOBILE_CN_PATTERN = Pattern.compile("^[1](([3][0-9])|([4][5,7,9])|([5][^4,6,9])|([6][6])|([7][3,5,6,7,8])|([8][0-9])|([9][8,9]))[0-9]{8}$");
        Pattern FIXED_TELEPHONE_CN_PATTERN = Pattern.compile("^(0\\d{2}-\\d{8}(-\\d{1,4})?)|(0\\d{3}-\\d{7,8}(-\\d{1,4})?)$");
        if (null == telephoneNumber && (MOBILE_CN_PATTERN.matcher(username).find() || FIXED_TELEPHONE_CN_PATTERN.matcher(username).find()))
            telephoneNumber = username;
        System.out.println(telephoneNumber);
    }
}