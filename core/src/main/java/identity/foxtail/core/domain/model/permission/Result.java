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

package identity.foxtail.core.domain.model.permission;

import java.util.StringJoiner;

/***
 * @author <a href="www.foxtail.cc/authors/guan xiangHuan">guan xiangHuan</a>
 * @since JDK8.0
 * @version 0.0.1 2019-01-27
 */
public class Result {
    private boolean permit;
    private ResultStatusCode code;
    private String message;

    /**
     * @param permit
     * @param message
     */
    public Result(boolean permit, String message) {
        this(permit, ResultStatusCode.OK, message);
    }

    /**
     * @param permit
     * @param code
     * @param message
     */
    public Result(boolean permit, ResultStatusCode code, String message) {
        this.permit = permit;
        this.code = code;
        setMessage(message);
    }

    /**
     * Gets the value of permit.
     *
     * @return the value of permit
     */
    public boolean isPermit() {
        return permit;
    }

    /**
     * Gets the value of code.
     *
     * @return the value of code
     */
    public ResultStatusCode code() {
        return code;
    }

    /**
     * Gets the value of message.
     *
     * @return the value of message
     */
    public String message() {
        return message;
    }

    /**
     * Sets the value of code.
     */
    private void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Result.class.getSimpleName() + "[", "]")
                .add("permit=" + permit)
                .add("code=" + code)
                .add("message='" + message + "'")
                .toString();
    }
}
