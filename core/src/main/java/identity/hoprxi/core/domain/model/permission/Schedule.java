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

/***
 * @author <a href="www.hoprxi.com/authors/guan xiangHuan">guan xiangHuan</a>
 * @since JDK8.0
 * @version 0.0.1 2019-01-21
 */
public class Schedule {
    private String cron;

    public Schedule(String cron) {
        this.cron = cron;
    }

    public boolean isInSchedule() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Schedule schedule = (Schedule) o;
        return cron != null ? cron.equals(schedule.cron) : schedule.cron == null;
    }

    public String cron() {
        return cron;
    }

    @Override
    public int hashCode() {
        return cron != null ? cron.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Schedule{" +
                "cron='" + cron + '\'' +
                '}';
    }
}
