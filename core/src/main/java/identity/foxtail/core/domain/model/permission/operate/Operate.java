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

/***
 * @author <a href="www.foxtail.cc/authors/guan xiangHuan">guan xiangHuan</a>
 * @since JDK8.0
 * @version 0.0.1 2019-01-19
 */
public class Operate {
    //such as:READ,DISCOUNT,MODIFY,PRINT                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      ,REMOVE
    private String name;
    private Strategy strategy;
    private Schedule schedule;

    public Operate(String name, Strategy strategy, Schedule schedule) {
        this.name = name;
        this.strategy = strategy;
        this.schedule = schedule;
    }

    public Operate(String name, Strategy strategy) {
        this.name = name;
        this.strategy = strategy;
    }

    public String name() {
        return name;
    }

    public Strategy strategy() {
        return strategy;
    }

    public Schedule schedule() {
        return schedule;
    }

    @Override
    public String toString() {
        return "Operate{" +
                "name='" + name + '\'' +
                ", strategy=" + strategy +
                ", schedule=" + schedule +
                '}';
    }
}
