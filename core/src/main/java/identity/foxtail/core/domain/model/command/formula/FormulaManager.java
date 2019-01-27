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
public class FormulaManager {
    private static Map<String, FunctionIntf> funcMap = new HashMap<String, FunctionIntf>();
    public final FunctionIntf OPEN_BOX = new FunctionIntf() {
        @Override
        public Result execute(VariantContext context) {
            return new Result(true, "passed");
        }
    };

    public static void registerFunction(String funcName, FunctionIntf func) {
    }

    public static void unRegisterFunction(String funcName) {
    }

    public static FunctionIntf queryFunction(String funcName) {
        return null;
    }
}
