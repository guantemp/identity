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

import java.util.StringJoiner;

/***
 * @author <a href="www.hoprxi.com/authors/guan xiangHuan">guan xiangHuan</a>
 * @since JDK8.0
 * @version 0.0.1 2019-01-27
 */
public class Result {
    public static final Result FORBIDDEN = new Result(ResultStatusCode.Forbidden, "Request denied");
    public static final Result PERMIT = new Result(ResultStatusCode.Permit, "Request allowed");
    public static final Result NO_CONTENT = new Result(ResultStatusCode.No_Content, "Do nothing.");
    private ResultStatusCode code;
    private String message;


    /**
     * @param code
     * @param message
     */
    public Result(ResultStatusCode code, String message) {
        this.code = code;
        setMessage(message);
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
                .add("code=" + code)
                .add("message='" + message + "'")
                .toString();
    }

    /**
     * @param result
     * @return
     */
    public Result or(Result result) {
        if (result.code == ResultStatusCode.Permit || (this.code == ResultStatusCode.Forbidden && result.code == ResultStatusCode.Forbidden))
            return result;
        return this;
    }
}
