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
        public boolean isExpired() {
            return false;
        }
    };
    private boolean enable;
    private LocalDateTime expiryDate;

    /**
     * @param enable
     * @param expiryDate
     * @throws IllegalArgumentException if expirationDate is past time
     */
    public Enablement(boolean enable, LocalDateTime expiryDate) {
        this.enable = enable;
        this.expiryDate = expiryDate;
        //setExpirationDate(expirationDate);
    }

    public static Enablement getInstance(boolean enable, LocalDateTime expirationDate) {
        if (enable && DAY_OF_INFAMY.isEqual(expirationDate))
            return Enablement.PERMANENCE;
        return new Enablement(enable, expirationDate);
    }

    private void setExpiryDate(LocalDateTime expiryDate) {
        if (expiryDate.isBefore(LocalDateTime.now().minusMinutes(3)))
            throw new IllegalArgumentException("The expiry date must be some time in the future.");
        this.expiryDate = expiryDate;
    }

    public boolean isEnable() {
        return enable;
    }

    public LocalDateTime expirationDate() {
        return expiryDate;
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiryDate);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Enablement that = (Enablement) o;

        if (enable != that.enable) return false;
        return expiryDate != null ? expiryDate.equals(that.expiryDate) : that.expiryDate == null;
    }

    @Override
    public int hashCode() {
        int result = (enable ? 1 : 0);
        result = 31 * result + (expiryDate != null ? expiryDate.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Enablement{" +
                "enable=" + enable +
                ", expirationDate=" + expiryDate +
                '}';
    }
}
