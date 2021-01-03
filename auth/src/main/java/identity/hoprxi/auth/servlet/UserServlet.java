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
import identity.hoprxi.core.application.command.RegisterUserCommand;
import identity.hoprxi.core.application.command.SocializationBindUserCommand;
import identity.hoprxi.core.domain.model.id.Enablement;
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

/***
 * @author <a href="www.hoprxi.com/authors/guan xianghuang">guan xiangHuan</a>
 * @since JDK8.0
 * @version 0.0.1 20180408
 */
@WebServlet(urlPatterns = {"/v1/user/*"}, name = "user", asyncSupported = false)
public class UserServlet extends HttpServlet {
    private static Cache<String, Integer> cache = CacheManager.buildCache("sms");

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo != null) {

        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = null;
        String password = null;
        String confirmPassword = null;
        String bindId = null;
        String thirdPartyName = null;
        int code = 0;
        String invitationCode = null;
        String method = "byCode";
        JsonFactory jasonFactory = new JsonFactory();
        JsonParser parser = jasonFactory.createParser(request.getInputStream());
        while (!parser.isClosed()) {
            JsonToken jsonToken = parser.nextToken();
            if (JsonToken.FIELD_NAME.equals(jsonToken)) {
                String fieldName = parser.getCurrentName();
                parser.nextToken();
                switch (fieldName) {
                    case "method":
                        method = parser.getValueAsString();
                        break;
                    case "code":
                        code = parser.getValueAsInt();
                        break;
                    case "invitationCode":
                        invitationCode = parser.getValueAsString();
                        break;
                    case "username":
                        username = parser.getValueAsString();
                        break;
                    case "password":
                        password = parser.getValueAsString();
                        break;
                    case "confirmPassword":
                        confirmPassword = parser.getValueAsString();
                        break;
                    case "bindId":
                        bindId = parser.getValueAsString();
                        break;
                    case "thirdPartyName":
                        thirdPartyName = parser.getValueAsString();
                        break;
                }
            }
        }
        response.setContentType("application/json; charset=UTF-8");
        JsonGenerator generator = jasonFactory.createGenerator(response.getOutputStream(), JsonEncoding.UTF8)
                .setPrettyPrinter(new DefaultPrettyPrinter());
        switch (method) {
            case "bind":
                socializationBindUser(generator, username, code, bindId, thirdPartyName);
                break;
            case "byCode":
                registerUser(generator, username, password, confirmPassword, code);
                break;
        }
        generator.flush();
        generator.close();
    }

    private void socializationBindUser(JsonGenerator generator, String username, int code, String bindId, String thirdPartyName) throws IOException {
        if (!checkUserName(username)) {
            generator.writeStartObject();
            generator.writeNumberField("code", 400);
            generator.writeStringField("message", "错误/非法的用户名");
            generator.writeEndObject();
            return;
        }
        Integer cachedCode = cache.get(username);
        if (cachedCode != null && cachedCode.intValue() != code) {
            generator.writeStartObject();
            generator.writeNumberField("code", 401);
            generator.writeStringField("message", "验证码不正确或已过期。");
            generator.writeEndObject();
            return;
        }
        UserApplicationService service = new UserApplicationService();
        SocializationBindUserCommand socializationBindUserCommand = new SocializationBindUserCommand(username, bindId, thirdPartyName);
        UserDescriptor userDescriptor = service.socializationBindUser(socializationBindUserCommand);
        this.outputUserDescriptor(generator, userDescriptor);
    }

    private void registerUser(JsonGenerator generator, String username, String password, String confirmPassword, int code) throws IOException {
        if (!checkUserName(username)) {
            generator.writeStartObject();
            generator.writeNumberField("code", 400);
            generator.writeStringField("message", "错误/非法的用户名");
            generator.writeEndObject();
            return;
        }
        Integer cachedCode = cache.get(username);
        if (cachedCode != null && cachedCode.intValue() != code) {
            generator.writeStartObject();
            generator.writeNumberField("code", 401);
            generator.writeStringField("message", "验证码不正确或已过期。");
            generator.writeEndObject();
            return;
        }
        if (!chekPassword(password, confirmPassword)) {
            generator.writeStartObject();
            generator.writeNumberField("code", 402);
            generator.writeStringField("message", "两次输入的密码不一致");
            generator.writeEndObject();
            return;
        }
        UserApplicationService service = new UserApplicationService();
        RegisterUserCommand registerUserCommand = new RegisterUserCommand(username, null, null, password, true, Enablement.PERMANENCE.deadline());
        UserDescriptor userDescriptor = service.registerUser(registerUserCommand);
        this.outputUserDescriptor(generator, userDescriptor);
    }

    private void outputUserDescriptor(JsonGenerator generator, UserDescriptor userDescriptor) throws IOException {
        if (userDescriptor == UserDescriptor.NullUserDescriptor) {
            generator.writeStartObject();
            generator.writeStringField("code", "401");
            generator.writeStringField("message", "用户未注册!");
            generator.writeEndObject();
        } else {
            //generator.writeStringField("referer", request.getHeader("Referer"));
            generator.writeStartObject();
            generator.writeStringField("token", "byShortcut");
            generator.writeObjectFieldStart("user");
            generator.writeStringField("id", userDescriptor.id());
            generator.writeStringField("username", userDescriptor.username());
            generator.writeBooleanField("available", userDescriptor.isAvailable());
            generator.writeEndObject();
            generator.writeEndObject();
        }
    }

    private boolean checkUserName(String username) {
        return Optional.ofNullable(username).map(u -> username.length() > 1 && username.length() <= 255).orElse(false);
    }

    private boolean chekPassword(String password, String confirmPassword) {
        return Optional.ofNullable(password).map(u -> confirmPassword).map(b -> password.equals(confirmPassword)).orElse(false);
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        super.doDelete(request, response);
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        super.doPut(request, response);
    }
}
