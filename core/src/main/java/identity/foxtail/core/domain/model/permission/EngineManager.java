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

package identity.foxtail.core.domain.model.permission;

import java.util.HashMap;
import java.util.Map;

/***
 * @author <a href="www.foxtail.cc/authors/guan xiangHuan">guan xiangHuan</a>
 * @since JDK8.0
 * @version 0.0.1 2019-01-27
 */
public class EngineManager {
    public static final Engine OPEN_BOX = context -> Result.PERMIT;
    public static final Engine REFUND = context -> Result.PERMIT;

    public static final Engine DISCOUNT = new Engine() {
        @Override
        public Result execute(VariantContext context) {
            Fuel fuel = context.<Fuel>getVariant("fuel");
            int preset = Integer.parseInt(fuel.formula().substring(6, 8));

            int rate = context.<Integer>getVariant("rate");
            if (rate >= preset)
                return new Result(ResultStatusCode.Permit, "It's good");
            else
                return new Result(ResultStatusCode.Forbidden, "Discount rate is too low:" + preset);
        }

        @Override
        public boolean isQualifiedFuel(Fuel fuel) {
            return true;
        }
    };
    private static Map<String, Engine> funcMap = new HashMap<String, Engine>();

    static {
        registerFunction("open_box", OPEN_BOX);
        registerFunction("打开钱箱", OPEN_BOX);
        registerFunction("refund", REFUND);
        registerFunction("退货", REFUND);
        registerFunction("discount", DISCOUNT);
        registerFunction("red_catalog", DISCOUNT);
    }

    public static void registerFunction(String funcName, Engine engine) {
        funcMap.put(funcName, engine);
    }

    public static void unRegisterFunction(String funcName) {
        funcMap.remove(funcName);
    }

    public static Engine queryEngine(String funcName) {
        return funcMap.get(funcName);
    }
}
