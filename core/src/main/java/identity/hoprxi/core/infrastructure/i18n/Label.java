/*
 * Copyright (c) 2019 www.hoprxi.com All rights Reserved.
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

package identity.hoprxi.core.infrastructure.i18n;


import mi.hoprxi.util.NLS;

/***
 * @author <a href="www.hoprxi.com/authors/guan xianghuang">guan xiangHuan</a>
 * @since JDK8.0
 * @version 0.0.1 builder 2019-01-15
 */
public class Label extends NLS {
    private static final String BUNDLE_NAME = "hr.foxtail.infrastructure.resource.label"; //$NON-NLS-1$

    public static String NATION_HAN;
    public static String PRIMARY_SCHOOL;

    static {
        NLS.initializeMessages(BUNDLE_NAME, Label.class);
    }
}
