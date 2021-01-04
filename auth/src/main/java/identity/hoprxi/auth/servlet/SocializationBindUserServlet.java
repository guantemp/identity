/*
 * Copyright (c) 2021 www.hoprxi.com All Rights Reserved.
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

package identity.hoprxi.auth.servlet;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import identity.hoprxi.core.application.UserApplicationService;
import identity.hoprxi.core.application.command.SocializationBindUserCommand;
import identity.hoprxi.core.domain.model.id.UserDescriptor;
import salt.hoprxi.cache.Cache;
import salt.hoprxi.cache.CacheManager;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

/**
 * @author <a href="www.hoprxi.com/author/guan xianghuang">guan xiangHuan</a>
 * @version 0.0.1 2021-01-04
 * @since JDK8.0
 */
@WebServlet(urlPatterns = {"/v1/bind"}, name = "bind", asyncSupported = false)
public class SocializationBindUserServlet extends HttpServlet {
    private static Cache<String, Integer> cache = CacheManager.buildCache("code");

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = null;
        String bindId = null;
        String thirdPartyName = null;
        String nickName = null;
        String avatarUrl = null;
        int code = 0;
        JsonFactory jasonFactory = new JsonFactory();
        JsonParser parser = jasonFactory.createParser(req.getInputStream());
        while (!parser.isClosed()) {
            JsonToken jsonToken = parser.nextToken();
            if (JsonToken.FIELD_NAME.equals(jsonToken)) {
                String fieldName = parser.getCurrentName();
                parser.nextToken();
                switch (fieldName) {
                    case "code":
                        code = parser.getValueAsInt();
                        break;
                    case "username":
                        username = parser.getValueAsString();
                        break;
                    case "bindId":
                        bindId = parser.getValueAsString();
                        break;
                    case "nickName":
                        nickName = parser.getValueAsString();
                        break;
                    case "avatarUrl":
                        avatarUrl = parser.getValueAsString();
                        break;
                    case "thirdPartyName":
                        thirdPartyName = parser.getValueAsString();
                        break;
                }
            }
        }
        resp.setContentType("application/json; charset=UTF-8");
        JsonGenerator generator = jasonFactory.createGenerator(resp.getOutputStream(), JsonEncoding.UTF8)
                .setPrettyPrinter(new DefaultPrettyPrinter());
        boolean checked = true;
        if (!checkUserName(username)) {
            generator.writeStartObject();
            generator.writeNumberField("code", 400);
            generator.writeStringField("message", "错误/非法的用户名");
            generator.writeEndObject();
            checked = false;
        }
        Integer cachedCode = cache.get(username);
        if (cachedCode == null || cachedCode.intValue() != code) {
            generator.writeStartObject();
            generator.writeNumberField("code", 401);
            generator.writeStringField("message", "验证码不正确或已过期。");
            generator.writeEndObject();
            checked = false;
        }
        if (checked) {
            UserApplicationService service = new UserApplicationService();
            SocializationBindUserCommand socializationBindUserCommand = new SocializationBindUserCommand(username, bindId, thirdPartyName);
            UserDescriptor userDescriptor = service.socializationBindUser(socializationBindUserCommand);
            //generator.writeStringField("referer", request.getHeader("Referer"));
            generator.writeStartObject();
            generator.writeStringField("token", "byShortcut");
            generator.writeObjectFieldStart("user");
            generator.writeStringField("id", userDescriptor.id());
            generator.writeStringField("username", userDescriptor.username());
            generator.writeBooleanField("available", userDescriptor.isAvailable());
            generator.writeStringField("nickName", nickName);
            generator.writeStringField("avatarUrl", avatarUrl);
            generator.writeStringField("thirdPartyName", "WECHAT");
            generator.writeEndObject();
            generator.writeEndObject();
        }
        generator.flush();
        generator.close();
    }

    private boolean checkUserName(String username) {
        return Optional.ofNullable(username).map(u -> username.length() > 1 && username.length() <= 255).orElse(false);
    }

}
