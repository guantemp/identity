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
package identity.hoprxi.core.consumer;

import identity.hoprxi.core.annotation.Encryption;

import java.lang.reflect.Field;

/***
 * @author <a href="www.hoprxi.com/author/guan xiangHuan">guan xiangHuan</a>
 * @since JDK8.0
 * @version 0.0.1 20180114
 */
public class EncryptionConsumer {
    public static void processEncryptionAnnotation(Object object) {
        Class<?> cl = object.getClass();
        for (Field field : cl.getDeclaredFields()) {
            //this field have Encryption annotation
            Encryption encryption = field.getAnnotation(Encryption.class);
            if (encryption != null) {
                try {
                    field.setAccessible(true);
                    field.set(object, encryption.value().newInstance());
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
