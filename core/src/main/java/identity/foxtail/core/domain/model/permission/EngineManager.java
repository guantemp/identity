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
    public static final Engine OPEN_BOX = context -> new Result(true, "passed");

    public static final Engine DISCOUNT = context -> {
        Result result = new Result(false, "不能低于4折,ok?");
        if (context != null) {
            String expression = context.getVariant("expression");
            // process expression
            int rate = context.getVariant("rate");
            if (rate >= 40 && rate <= 100)
                result = new Result(true, "it's good");
        }
        return result;
    };
    private static Map<String, Engine> funcMap = new HashMap<String, Engine>();

    static {
        registerFunction("open_box", OPEN_BOX);
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
