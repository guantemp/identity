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

package identity.hoprxi.core.domain.model.permission;

import java.util.HashMap;
import java.util.Map;

/***
 * @author <a href="www.hoprxi.com/authors/guan xiangHuan">guan xiangHuan</a>
 * @since JDK8.0
 * @version 0.0.1 2019-01-26
 */
public class VariantContext {
    private Map<String, Object> variantMap = new HashMap<>();

    public <T> T getVariant(String key) {
        return (T) variantMap.get(key);
    }

    public VariantContext put(String key, Object value) {
        variantMap.put(key, value);
        return this;
    }
}
