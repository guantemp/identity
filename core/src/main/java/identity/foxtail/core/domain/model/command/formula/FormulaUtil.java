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

package identity.foxtail.core.domain.model.command.formula;

import identity.foxtail.core.domain.model.command.VariantContext;

import java.util.HashMap;
import java.util.Map;

/***
 * @author <a href="www.foxtail.cc/authors/guan xiangHuan">guan xiangHuan</a>
 * @since JDK8.0
 * @version 0.0.1 2019-01-27
 */
public class FormulaUtil {


    public final FunctionIntf<Boolean> OPEN_BOX = new FunctionIntf<Boolean>() {
        @Override
        public Boolean execute(VariantContext context) {
            return null;
        }
    };
    public final FunctionIntf<Boolean> DISCOUNT = new FunctionIntf<Boolean>() {
        @Override
        public Boolean execute(VariantContext context) {
            return null;
        }
    };
    public final FunctionIntf<Boolean> RED_CATALOG = new FunctionIntf<Boolean>() {
        @Override
        public Boolean execute(VariantContext context) {
            return null;
        }
    };
    private Map<String, FunctionIntf> funcMap = new HashMap<String, FunctionIntf>();

    public FormulaUtil() {
        funcMap.put("openbox", OPEN_BOX);
        funcMap.put("openbox", DISCOUNT);
        funcMap.put("openbox", RED_CATALOG);
    }
}
