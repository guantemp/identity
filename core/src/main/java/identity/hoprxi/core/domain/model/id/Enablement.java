/*
 * Copyright (c) 2020 www.hoprxi.com All Rights Reserved.
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
package identity.hoprxi.core.domain.model.id;

import java.time.LocalDateTime;

/***
 * @author <a href="www.hoprxi.com/authors/guan xiangHuan">guan xiangHuan</a>
 * @since JDK8.0
 * @version 0.0.3 2020-12-13
 */
public class Enablement {
    private static final LocalDateTime DAY_OF_INFAMY = LocalDateTime.of(2015, 3, 26, 0, 0);
    public static final Enablement PERMANENCE = new Enablement(true, DAY_OF_INFAMY) {
        @Override
        public boolean isOverdue() {
            return false;
        }
    };
    private boolean enable;
    private LocalDateTime deadline;

    /**
     * @param enable
     * @param deadline
     * @throws IllegalArgumentException if expirationDate is past time
     */
    public Enablement(boolean enable, LocalDateTime deadline) {
        this.enable = enable;
        setDeadline(deadline);
    }

    public static Enablement getInstance(boolean enable, LocalDateTime deadline) {
        if (enable && DAY_OF_INFAMY.isEqual(deadline))
            return Enablement.PERMANENCE;
        return new Enablement(enable, deadline);
    }

    private void setDeadline(LocalDateTime deadline) {
        if (!deadline.isEqual(DAY_OF_INFAMY) && deadline.isBefore(LocalDateTime.now()))
            throw new IllegalArgumentException("The expiry date must be some time in the future.");
        this.deadline = deadline;
    }

    public boolean isEnable() {
        return enable;
    }

    public LocalDateTime deadline() {
        return deadline;
    }

    public boolean isOverdue() {
        return LocalDateTime.now().isAfter(deadline);
    }

    public Enablement changeDeadline(LocalDateTime newExpiryDate) {
        if (newExpiryDate == null || newExpiryDate.isBefore(LocalDateTime.now()))
            return this;
        return new Enablement(enable, newExpiryDate);
    }

    public Enablement disable() {
        return new Enablement(false, deadline);
    }

    public Enablement enable() {
        return new Enablement(true, deadline);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Enablement that = (Enablement) o;

        if (enable != that.enable) return false;
        return deadline != null ? deadline.equals(that.deadline) : that.deadline == null;
    }

    @Override
    public int hashCode() {
        int result = (enable ? 1 : 0);
        result = 31 * result + (deadline != null ? deadline.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Enablement{" +
                "enable=" + enable +
                ", deadline=" + deadline +
                '}';
    }
}
