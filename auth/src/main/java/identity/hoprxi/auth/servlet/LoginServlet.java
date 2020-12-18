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
import identity.hoprxi.core.application.UserApplicationService;
import identity.hoprxi.core.domain.model.id.UserDescriptor;
import salt.hoprxi.cache.Cache;
import salt.hoprxi.utils.NumberHelper;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

/***
 * @author <a href="www.hoprxi.com/authors/guan xianghuang">guan xiangHuan</a>
 * @since JDK8.0
 * @version 0.0.1 2020-12-08
 */
@WebServlet(urlPatterns = {"/v1/login"}, name = "login", asyncSupported = false, initParams = {
        @WebInitParam(name = "max_error_times", value = "5"), @WebInitParam(name = "cookie_expired", value = "300")})
public class LoginServlet extends HttpServlet {
    private static int max_error_times; // error times
    private static int cookie_expired;//five Minutes
    private static Cache<String, Integer> cache = null;

    @Override
    public void init() throws ServletException {
        super.init();
        ServletConfig config = getServletConfig();
        if (config != null) {
            max_error_times = NumberHelper.intOf(config.getInitParameter("max_error_times"), 3);
            cookie_expired = NumberHelper.intOf(config.getInitParameter("max_error_times"), 300);
        }
    }

    /**
     * @param username
     * @param password
     * @return
     */
    private boolean validate(String username, String password) {
        username = username.trim();
        password = password.trim();
        if (username == null || username.isEmpty() || username.length() < 3 || username.length() > 255 ||
                password == null || password.isEmpty() || password.length() < 6 || password.length() > 40)
            return false;
        return true;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        response.setContentType("application/json; charset=UTF-8");
        JsonFactory jasonFactory = new JsonFactory();
        JsonGenerator generator = jasonFactory.createGenerator(response.getOutputStream(), JsonEncoding.UTF8)
                .setPrettyPrinter(new DefaultPrettyPrinter());
        generator.writeStartObject();
        generator.writeStringField("redirectUrl", response.encodeURL(request.getHeader("Referer")));
        generator.writeStringField("session", session.getId());
        generator.writeNumberField("notice_interval", 5000);
        generator.writeStringField("title", "登录");

        generator.writeFieldName("notice");
        generator.writeStartArray();
        generator.writeStartObject();
        generator.writeStringField("image", "//cdn-www.lk361.com/www/Images/account/logon/computer01.png");
        generator.writeStringField("href", "https://bbs.lk361.com/forum.php?mod=viewthread&tid=150");
        generator.writeStringField("alt", "在线进销存,进销存软件,经销商管理,scrm管理系统,sass进销存软件,进销存软件哪个好,进销存软件免费版");
        generator.writeEndObject();

        generator.writeStartObject();
        generator.writeStringField("image", "//cdn-www.lk361.com/www/Images/account/logon/computer02.png");
        generator.writeStringField("href", "https://bbs.lk361.com/thread-162-1-1.html");
        generator.writeStringField("alt", "在线进销存,进销存软件,经销商管理,crm管理系统,sass进销存软件,进销存软件哪个好,进销存软件免费版");
        generator.writeEndObject();
        generator.writeEndArray();

        generator.writeObjectFieldStart("openid");
        generator.writeObjectFieldStart("wechat");
        generator.writeStringField("icon", "http://pic1.16pic.com/00/00/17/16pic_17604_b.jpg");
        generator.writeStringField("appid", "wx325465");
        generator.writeEndObject();
        generator.writeObjectFieldStart("qq");
        generator.writeStringField("icon", "http://www.wetchat.com//");
        generator.writeStringField("appid", "qq325dfgdf465");
        generator.writeEndObject();
        generator.writeObjectFieldStart("sina");
        generator.writeStringField("icon", "http://www.wetchat.com//");
        generator.writeStringField("token", "wxdsg325465");
        generator.writeEndObject();
        generator.writeEndObject();
        generator.writeEndObject();
        generator.close();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = null;
        String password = null;
        JsonFactory jasonFactory = new JsonFactory();
        JsonParser parser = jasonFactory.createParser(request.getInputStream());
        while (!parser.isClosed()) {
            JsonToken jsonToken = parser.nextToken();
            if (JsonToken.FIELD_NAME.equals(jsonToken)) {
                String fieldName = parser.getCurrentName();
                parser.nextToken();
                switch (fieldName) {
                    case "username":
                        username = parser.getValueAsString();
                        break;
                    case "password":
                        password = parser.getValueAsString();
                        break;
                }
            }
        }
        response.setContentType("application/json; charset=UTF-8");
        JsonGenerator generator = jasonFactory.createGenerator(response.getOutputStream(), JsonEncoding.UTF8)
                .setPrettyPrinter(new DefaultPrettyPrinter());
        if (validate(username, password)) {
            UserApplicationService service = new UserApplicationService();
            UserDescriptor userDescriptor = service.authenticate(username, password);
            if (userDescriptor == UserDescriptor.NullUserDescriptor) {
                HttpSession session = request.getSession(true);
                int errorTimes = NumberHelper.intOf((String) session.getAttribute(username), 0);
                if (errorTimes > max_error_times) {
                    Cookie cookie = new Cookie("required_captcha", "true");
                    cookie.setMaxAge(cookie_expired);
                    response.addCookie(cookie);
                }
                session.setAttribute(username, errorTimes + 1);
                //cache.put(username, errorTimes + 1);
                generator.writeStartObject();
                generator.writeStringField("code", "401");
                generator.writeStringField("msg", "unverified username or password is mismatch");
                generator.writeEndObject();
            } else {
                generator.writeStartObject();
                generator.writeStringField("code", "200");
                generator.writeStringField("msg", "ok");
                generator.writeStringField("referer", request.getHeader("Referer"));
                generator.writeObjectFieldStart("user");
                generator.writeStringField("id", userDescriptor.id());
                generator.writeStringField("username", userDescriptor.username());
                generator.writeBooleanField("available", userDescriptor.isAvailable());
                generator.writeEndObject();
                generator.writeEndObject();
            }
        } else {
            generator.writeStringField("code", "400");
            generator.writeStringField("msg", "Wrong request format");
        }
        generator.flush();
        generator.close();
    }
}
