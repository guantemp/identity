/*
 *  Copyright 2018 www.foxtail.cc All rights Reserved.
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
package identity.foxtail.core.domain.model.id;

import java.time.LocalDateTime;

/***
 * @author <a href="www.foxtail.cc/authors/guan xiangHuan">guan xiangHuan</a>
 * @since JDK8.0
 * @version 0.0.2 2018-11-13
 */
public class Enablement {
    public static final Enablement FOREVER = new Enablement(true, LocalDateTime.of(9999, 12, 31, 23, 59, 59, 999999999)) {
        @Override
        public boolean isExpired() {
            return false;
        }
    };
    private boolean enable;
    private LocalDateTime expiryDateTime;

    /**
     * @param enable
     * @param expiryDateTime
     * @throws IllegalArgumentException if expiryDateTime is past time
     */
    public Enablement(boolean enable, LocalDateTime expiryDateTime) {
        this.enable = enable;
        this.expiryDateTime = expiryDateTime;
        //setExpiryDateTime(expiryDateTime);
    }

    private void setExpiryDateTime(LocalDateTime expiryDateTime) {
        if (expiryDateTime.isBefore(LocalDateTime.now().minusMinutes(15)))
            throw new IllegalArgumentException("The expiry date must be some time in the future.");
        this.expiryDateTime = expiryDateTime;
    }

    public boolean isEnable() {
        return enable;
    }

    public LocalDateTime expiryDateTime() {
        return expiryDateTime;
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiryDateTime);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Enablement that = (Enablement) o;

        if (enable != that.enable) return false;
        return expiryDateTime != null ? expiryDateTime.equals(that.expiryDateTime) : that.expiryDateTime == null;
    }

    @Override
    public int hashCode() {
        int result = (enable ? 1 : 0);
        result = 31 * result + (expiryDateTime != null ? expiryDateTime.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Enablement{" +
                "enable=" + enable +
                ", expiryDateTime=" + expiryDateTime +
                '}';
    }
}
