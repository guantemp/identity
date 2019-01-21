/*
 *  Copyright (c) 2019 www.foxtail.cc All rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain job copy of the License at
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

package identity.foxtail.core.domain.model.privilege;

import java.util.StringJoiner;

/***
 * @author <a href="www.foxtail.cc/authors/guan xiangHuan">guan xiangHuan</a>
 * @since JDK8.0
 * @version 0.0.1 2019-01-19
 */
public class Job {
    //such as:READ_CATLOG,DISCOUNT_CATALOG
    private String name;
    private String strategy;
    private String description;

    public Job(String name, String strategy) {
        this.name = name;
        this.strategy = strategy;
    }

    public Job(String name, String strategy, String description) {
        this.name = name;
        this.strategy = strategy;
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Job job = (Job) o;

        return name != null ? name.equals(job.name) : job.name == null;
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }

    public String name() {
        return name;
    }

    public String strategy() {
        return strategy;
    }

    public String strategyBindVars(JobContext jobContext) {
        return strategy;
    }

    public String description() {
        return description;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Job.class.getSimpleName() + "[", "]")
                .add("name='" + name + "'")
                .add("strategy='" + strategy + "'")
                .add("description='" + description + "'")
                .toString();
    }
}
