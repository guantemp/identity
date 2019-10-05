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
package identity.hoprxi.core.infrastruture.servers;

import mi.hoprxi.crypto.BcryptEncryption;
import mi.hoprxi.crypto.Pbhkdf2Encryption;
import mi.hoprxi.crypto.ScryptEncryption;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/***
 * @author <a href="www.hoprxi.com/author/guan xianghuan">guan xiangHuan</a>
 * @since JDK7.0
 * @version 0.0.1 20180121
 */
public class EncryptionServiceTest {

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @Test
    public void testCheckWithBcrpt() {
        assertTrue(
                new BcryptEncryption().check("中文测试", "$2a$12$LOzBbyR6rCgynJPKQ.2mdeokCAdTNejb.rY0dIWbJlmwyAigKu4si"));
        assertFalse(
                new BcryptEncryption().check("Qwe12346", "$2a$12$Tpi47sgOMW4dzZXagumdfuCDsZYot2pdt9v8PT7fWV9jC.hXnDPnS"));
    }

    @Test
    public void testEncryptWithBcrpt() {
        System.out.println("BCrypt encoding :" + new BcryptEncryption().encrypt("中文测试我撒西费士大夫赛usdafosw撒肺结核"));
        System.out.println("BCrypt encoding :" + new BcryptEncryption().encrypt("password"));
    }

    @Test
    public void testCheckWithPbhkdf2() {
        assertTrue(new Pbhkdf2Encryption().check("password",
                "02ade2e8baacd8a57f37ae0d23a0c8e8:0aef25fc829b660e04bfc4aa111c564fd379b2ef55a96c8d7d11cee40b641f19"));
        assertFalse(new Pbhkdf2Encryption().check("Password",
                "02ade2e8baacd8a57f37ae0d23a0c8e8:0aef25fc829b660e04bfc4aa111c564fd379b2ef55a96c8d7d11cee40b641f10"));
        assertFalse(new Pbhkdf2Encryption().check("password",
                "02ade2e8baacd8a57f37ae0d23a0c8e8:0aef25fc829b660e04bfc4aa111c564fd379b2ef55a96c8d7d11cee41b641f19"));
    }

    @Test
    public void testEncryptWithPbhkdf2() {
        System.out.println("PBHKDF2 encoding:" + new Pbhkdf2Encryption().encrypt("中文测试我撒西数萨福克郡水电费士大夫赛usdafosw撒肺结核"));
        System.out.println("PBHKDF2 encoding(password):" + new Pbhkdf2Encryption().encrypt("password"));
    }

    @Test
    public void testEncryptWithScrypt() {
        System.out.println("Scrypt encoding:" + new ScryptEncryption().encrypt("中文测试我撒西数萨福克郡水电费士大夫赛usdafosw撒肺结核"));
        System.out.println("Scrypt encoding(\"anonymous\"):" + new ScryptEncryption().encrypt("anonymous"));
        //illegal
        //System.out.println("Scrypt encoding:" + new ScryptEncryption().encrypt(""));
    }

    @Test
    public void testCheckWithScrypt() {
        assertTrue(
                new ScryptEncryption().check("中文测试我撒西数萨福克郡水电费士大夫赛usdafosw撒肺结核", "$s0$e0801$Zd03KWi7V6oZtSvRbzsbhg==$l6811J+Id5ST6on5m0o8l+8w/o5B+GyK+dknP4saOi0="));
        assertFalse(new ScryptEncryption().check("password", "$s0$e0801$Ul5rrRBlfwUyEJh2VrqDrw==$0RkpNoHHjbxCLJdgWHGJ1hCnxOvnlfPdvMpFhuGBPNS="));
    }
}

