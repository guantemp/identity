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

package identity.foxtail.core.domain.model.command;

/***
 * @author <a href="www.foxtail.cc/authors/guan xiangHuan">guan xiangHuan</a>
 * @since JDK8.0
 * @version 0.0.1 2019-01-27
 */
public class Formula {
    private static final String QUERY = "WITH user,act,role,permission,resource\n" +
            "FOR v,e,p IN 1..2 OUTBOUND @start  act,permission FILTER p.edges[1].job.name == 'open'" +
            "FILTER p.vertices[1]._key == @roleId and p.vertex[2]._key == @resourceId RETURN p.edges[1]";
    private String formula;
    public static final Formula EMPTY_FORMULA = new Formula("", null);
    private FunctionIntf function;

    public Formula(String formula, FunctionIntf function) {
        this.formula = formula;
        this.function = function;
    }

    Result execute(VariantContext context) {
        return function.execute(context);
    }
}
