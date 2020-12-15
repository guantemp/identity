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

import com.arangodb.entity.DocumentField;

import java.util.Objects;

/**
 * @author <a href="www.hoprxi.com/author/guan xianghuang">guan xiangHuan</a>
 * @version 0.0.1 2020-12-13
 * @since JDK8.0
 */
public class Socialization {
    @DocumentField(DocumentField.Type.KEY)
    private String unionId;
    private String userId;
    private ThirdParty thirdParty;

    protected Socialization(String unionId, String userId, ThirdParty thirdParty) {
        setUnionId(unionId);
        this.userId = userId;
        setThirdParty(thirdParty);
    }

    private void setThirdParty(ThirdParty thirdParty) {
        if (thirdParty == null)
            thirdParty = ThirdParty.UNKNOWN;
        this.thirdParty = thirdParty;
    }

    private void setUnionId(String unionId) {
        this.unionId = Objects.requireNonNull(unionId, "unionId is required");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Socialization that = (Socialization) o;

        return unionId != null ? unionId.equals(that.unionId) : that.unionId == null;
    }

    @Override
    public int hashCode() {
        return unionId != null ? unionId.hashCode() : 0;
    }

    public enum ThirdParty {
        WECHAT, QQ, SINA_WEI_BO, XIAO_MI, APPLE, GIT, UNKNOWN;

        /**
         * @param s
         * @return
         */
        public static ThirdParty of(String s) {
            for (ThirdParty thirdParty : values()) {
                if (thirdParty.toString().equals(s))
                    return thirdParty;
            }
            return ThirdParty.UNKNOWN;
        }
    }
}
