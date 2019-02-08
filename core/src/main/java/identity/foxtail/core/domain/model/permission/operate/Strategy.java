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

package identity.foxtail.core.domain.model.permission.operate;

import com.arangodb.velocypack.annotations.Expose;
import identity.foxtail.core.domain.model.permission.Result;

/***
 * @author <a href="www.foxtail.cc/authors/guan xiangHuan">guan xiangHuan</a>
 * @since JDK8.0
 * @version 0.0.1 2019-01-27
 */
public class Strategy {
    public static final Strategy NO_STRATEGY = new Strategy("", context -> new Result(true, "It's passed"));
    @Expose(serialize = false, deserialize = false)
    private Engine engine;
    private String formula;

    public Strategy(String formula, Engine engine) {
        this.formula = formula;
        this.engine = engine;
    }

    public Result execute(VariantContext context) {
        context.put("formula", formula);
        return engine.execute(context);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Strategy strategy = (Strategy) o;

        return formula != null ? formula.equals(strategy.formula) : strategy.formula == null;
    }

    @Override
    public int hashCode() {
        return formula != null ? formula.hashCode() : 0;
    }

    public String expression() {
        return formula;
    }

    @Override
    public String toString() {
        return "Strategy{" +
                "engine=" + engine +
                ", formula='" + formula + '\'' +
                '}';
    }
}
