/*
 *  Copyright 2018 www.hoprxi.com All rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
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
package identity.hoprxi.core.domain.model.id;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/***
 * @author <a href="www.hoprxi.com/authors/guan xiangHuan">guan xiangHuan</a>
 * @since JDK8.0
 * @version 0.0.2 2018-11-13
 */
public class Enablement {
    public static final Enablement FOREVER = new Enablement(true, LocalDateTime.parse("9999-12-31T23:59:59.999Z", DateTimeFormatter.ISO_ZONED_DATE_TIME)) {
        @Override
        public boolean isExpired() {
            return false;
        }
    };
    private boolean enable;
    private LocalDateTime expirationDate;

    /**
     * @param enable
     * @param expirationDate
     * @throws IllegalArgumentException if expirationDate is past time
     */
    public Enablement(boolean enable, LocalDateTime expirationDate) {
        this.enable = enable;
        this.expirationDate = expirationDate;
        //setExpirationDate(expirationDate);
    }

    public Enablement(boolean enable) {
        this(enable, LocalDateTime.now().plusMinutes(30));
    }

    private void setExpirationDate(LocalDateTime expirationDate) {
        if (expirationDate.isBefore(LocalDateTime.now().minusMinutes(1)))
            throw new IllegalArgumentException("The expiry date must be some time in the future.");
        this.expirationDate = expirationDate;
    }

    public boolean isEnable() {
        return enable;
    }

    public LocalDateTime expirationDate() {
        return expirationDate;
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expirationDate);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Enablement that = (Enablement) o;

        if (enable != that.enable) return false;
        return expirationDate != null ? expirationDate.equals(that.expirationDate) : that.expirationDate == null;
    }

    @Override
    public int hashCode() {
        int result = (enable ? 1 : 0);
        result = 31 * result + (expirationDate != null ? expirationDate.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Enablement{" +
                "enable=" + enable +
                ", expirationDate=" + expirationDate +
                '}';
    }
}
