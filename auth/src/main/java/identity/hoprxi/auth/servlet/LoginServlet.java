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
import identity.hoprxi.core.domain.model.id.UserDescriptor;
import salt.hoprxi.cache.Cache;
import salt.hoprxi.cache.CacheManager;
import salt.hoprxi.utils.NumberHelper;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Optional;
import java.util.regex.Pattern;

/***
 * @author <a href="www.hoprxi.com/authors/guan xianghuang">guan xiangHuan</a>
 * @since JDK8.0
 * @version 0.0.1 2020-12-08
 */
@WebServlet(urlPatterns = {"/v1/login"}, name = "login", asyncSupported = false, initParams = {
        @WebInitParam(name = "auth_error_times", value = "3")})
public class LoginServlet extends HttpServlet {
    private static int auth_error_times = 3; // error times
    //(?=.*[~@#$%\*-\+=:,\\?\[\]\{}]) 去除特殊字符
    private static Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{6,16}$");
    private static Cache<String, Integer> smsCache = CacheManager.buildCache("sms");
    private static Cache<String, Integer> captchaCache = CacheManager.buildCache("captcha");

    @Override
    public void init() throws ServletException {
        super.init();
        ServletConfig config = getServletConfig();
        if (config != null) {
            auth_error_times = NumberHelper.intOf(config.getInitParameter("auth_error_times"), 3);
        }
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
        String method = "byPassword";
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
                    case "method":
                        method = parser.getValueAsString();
                        break;
                }
            }
        }
        response.setContentType("application/json; charset=UTF-8");
        JsonGenerator generator = jasonFactory.createGenerator(response.getOutputStream(), JsonEncoding.UTF8)
                .setPrettyPrinter(new DefaultPrettyPrinter());
        switch (method) {
            case "byPassword":
                loginByPassword(generator, username, password, request.getHeader("Referer"));
                break;
            case "bySmsCode":
                request.getRequestDispatcher("/v1/sms").forward(request, response);
                break;
        }
        generator.flush();
        generator.close();
    }

    private void loginByPassword(JsonGenerator generator, String username, String password, String referer) throws IOException {
        if (!checkUserName(username)) {
            generator.writeStartObject();
            generator.writeNumberField("code", 400);
            generator.writeStringField("message", "错误/非法的用户名");
            generator.writeEndObject();
            return;
        }
        if (!checkPassword(password)) {
            generator.writeStartObject();
            generator.writeNumberField("code", 401);
            generator.writeStringField("message", "密码长度为6-16位，包含大，小写字母和数字");
            generator.writeEndObject();
            return;
        }
        UserApplicationService service = new UserApplicationService();
        UserDescriptor userDescriptor = service.authenticate(username, password);
        if (userDescriptor == UserDescriptor.NullUserDescriptor) {
            Integer auth_error_times = captchaCache.get(username);
            if (auth_error_times != null) {
                captchaCache.put(username, auth_error_times++);
            }
            generator.writeStartObject();
            generator.writeStringField("code", "402");
            generator.writeStringField("message", "用户名/密码不匹配！");
            generator.writeEndObject();
        } else {
            generator.writeStartObject();
            generator.writeStringField("referer", referer);
            generator.writeStringField("token", "byPassword");
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

    private boolean checkPassword(String password) {
        return Optional.ofNullable(password).map(u -> PASSWORD_PATTERN.matcher(password).matches()).orElse(false);
    }
}
