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
package identity.hoprxi.auth.servlet;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import identity.hoprxi.core.domain.model.id.UserDescriptor;

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
@WebServlet(urlPatterns = {"/v1/users/*"}, name = "users", asyncSupported = false)
public class UserServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo != null) {

        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String command = "registerByPassword";
        String username = null;
        String password = null;
        String confirmPassword = null;
        String unionId = null;
        String thirdParty = null;
        int smsCode = 0;
        JsonFactory jasonFactory = new JsonFactory();
        JsonParser parser = jasonFactory.createParser(request.getInputStream());
        while (!parser.isClosed()) {
            JsonToken jsonToken = parser.nextToken();
            if (JsonToken.FIELD_NAME.equals(jsonToken)) {
                String fieldName = parser.getCurrentName();
                parser.nextToken();
                switch (fieldName) {
                    case "command":
                        command = parser.getValueAsString();
                        break;
                    case "smsCode":
                        smsCode = parser.getValueAsInt();
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
                    case "unionId":
                        unionId = parser.getValueAsString();
                        break;
                    case "thirdParty":
                        thirdParty = parser.getValueAsString();
                        break;
                }
            }
        }
        response.setContentType("application/json; charset=UTF-8");
        JsonGenerator generator = jasonFactory.createGenerator(response.getOutputStream(), JsonEncoding.UTF8)
                .setPrettyPrinter(new DefaultPrettyPrinter());
        UserDescriptor userDescriptor = UserDescriptor.NullUserDescriptor;
        switch (command) {
            case "bind":
                break;
            case "registerByPassword":
                userDescriptor = registerUserByPassword(generator, username, password, confirmPassword);
                break;
            case "registerBySmsCode":
                break;
        }

        if (userDescriptor == UserDescriptor.NullUserDescriptor) {
            generator.writeStringField("error", "400");
            generator.writeStringField("msg", "The user not create");
        } else {
            response.setStatus(HttpServletResponse.SC_CREATED);
            generator.writeStringField("redirectUrl", response.encodeURL(request.getHeader("Referer")));
        }
        generator.flush();
        generator.close();
    }

    private UserDescriptor registerUserByPassword(JsonGenerator generator, String username, String password, String confirmPassword) throws IOException {
        if (!checkUserName(username)) {
            generator.writeStartObject();
            generator.writeNumberField("code", 400);
            generator.writeStringField("msg", "错误/非法的用户名");
            generator.writeEndObject();
            return UserDescriptor.NullUserDescriptor;
        }
        if (!chekPassword(password, confirmPassword)) {
            generator.writeStartObject();
            generator.writeNumberField("code", 400);
            generator.writeStringField("msg", "密码不符合规范");
            generator.writeEndObject();
            return UserDescriptor.NullUserDescriptor;
        }
        return UserDescriptor.NullUserDescriptor;
    }

    private boolean checkUserName(String username) {
        return Optional.ofNullable(username).map(u -> username.length() > 1 && username.length() <= 255).orElse(false);
    }

    private boolean chekPassword(String password, String confirmPassword) {
        Optional.ofNullable(password).map(u -> confirmPassword).map(b -> password.equals(confirmPassword)).orElse(false);
        return false;
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
