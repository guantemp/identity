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
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

/**
 * @author <a href="www.hoprxi.com/author/guan xianghuang">guan xiangHuan</a>
 * @version 0.0.1 2020-12-09
 * @since JDK8.0
 */
@WebServlet(urlPatterns = {"/v1/wxAuth"}, name = "wxAuth", asyncSupported = false, initParams = {
        @WebInitParam(name = "appid", value = "wx16772085b996e8e0"),
        @WebInitParam(name = "secret", value = "")})
public class WxAuthServlet extends HttpServlet {
    private static String appid = null;
    private static String secret = null;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        if (config != null) {
            appid = config.getInitParameter("appid");
            secret = config.getInitParameter("secret");
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doGet(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String js_code = null;
        String nickName = null;
        String avatarUrl = null;
        JsonFactory jasonFactory = new JsonFactory();
        JsonParser parser = jasonFactory.createParser(req.getInputStream());
        while (!parser.isClosed()) {
            JsonToken jsonToken = parser.nextToken();
            if (JsonToken.FIELD_NAME.equals(jsonToken)) {
                String fieldName = parser.getCurrentName();
                parser.nextToken();
                switch (fieldName) {
                    case "js_code":
                        js_code = parser.getValueAsString();
                        break;
                    case "nickName":
                        nickName = parser.getValueAsString();
                        break;
                    case "avatarUrl":
                        avatarUrl = parser.getValueAsString();
                        break;
                }
            }
        }
        resp.setContentType("application/json; charset=UTF-8");
        JsonGenerator generator = jasonFactory.createGenerator(resp.getOutputStream(), JsonEncoding.UTF8)
                .setPrettyPrinter(new DefaultPrettyPrinter());
        if (js_code == null || js_code.isEmpty()) {
            generator.writeStartObject();
            generator.writeNumberField("code", 400);
            generator.writeStringField("message", "非法凭证号：" + js_code);
            generator.writeEndObject();
        } else {
            try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
                URI uri = new URIBuilder()
                        .setScheme("https")
                        .setHost("api.weixin.qq.com")
                        .setPathSegments("sns", "jscode2session")
                        .setParameter("grant_type", "authorization_code")
                        .setParameter("appid", appid)
                        .setParameter("secret", secret)
                        .setParameter("js_code", js_code)
                        .build();
                HttpGet httpGet = new HttpGet(uri);
                try (CloseableHttpResponse response = httpclient.execute(httpGet)) {
                    HttpEntity entity = response.getEntity();
                    if (entity != null) {
                        String jsonResult = EntityUtils.toString(entity, StandardCharsets.UTF_8);
                        EntityUtils.consume(entity);
                        response.close();
                        this.ok(generator, jsonResult, nickName, avatarUrl);
                    }
                }
            } catch (URISyntaxException e) {
                generator.writeStartObject();
                generator.writeNumberField("code", 401);
                generator.writeStringField("message", "微信服务器离线!");
                generator.writeEndObject();
            }
        }
        generator.flush();
        generator.close();
    }

    private void ok(JsonGenerator generator, String json, String nickName, String avatarUrl) throws IOException {
        String openId = null;
        String session_key = null;
        String unionId = null;
        JsonFactory jasonFactory = new JsonFactory();
        JsonParser parser = jasonFactory.createParser(json);
        while (!parser.isClosed()) {
            JsonToken jsonToken = parser.nextToken();
            if (JsonToken.FIELD_NAME.equals(jsonToken)) {
                String fieldName = parser.getCurrentName();
                parser.nextToken();
                switch (fieldName) {
                    case "openid":
                        openId = parser.getValueAsString();
                        break;
                    case "session_key":
                        session_key = parser.getValueAsString();
                        break;
                    case "unionid":
                        unionId = parser.getValueAsString();
                        break;
                }
            }
        }
        if (unionId != null) {
            UserApplicationService service = new UserApplicationService();
            UserDescriptor userDescriptor = service.authenticateByThirdParty(unionId);
            if (userDescriptor == UserDescriptor.NullUserDescriptor) {//check openId
                userDescriptor = service.authenticateByThirdParty(openId);
                if (userDescriptor == UserDescriptor.NullUserDescriptor) {
                    generator.writeStartObject();
                    generator.writeNumberField("code", 300);
                    generator.writeStringField("message", "账号未绑定!");
                    generator.writeStringField("bindId", openId);
                    generator.writeEndObject();
                } else {//openId has bound
                    SocializationBindUserCommand socializationBindUserCommand = new SocializationBindUserCommand(userDescriptor.username(),
                            unionId, "WECHAT");
                    service.socializationBindUser(socializationBindUserCommand);
                    this.authenticatedUser(generator, nickName, avatarUrl, unionId, userDescriptor);
                }
            } else {
                this.authenticatedUser(generator, nickName, avatarUrl, unionId, userDescriptor);
            }
        } else { //use openId
            UserApplicationService service = new UserApplicationService();
            UserDescriptor userDescriptor = service.authenticateByThirdParty(openId);
            if (userDescriptor == UserDescriptor.NullUserDescriptor) {
                generator.writeStartObject();
                generator.writeNumberField("code", 300);
                generator.writeStringField("message", "账号未绑定!");
                generator.writeObjectFieldStart("user");
                generator.writeStringField("bindId", openId);
                generator.writeStringField("nickName", nickName);
                generator.writeStringField("avatarUrl", avatarUrl);
                generator.writeEndObject();
                generator.writeEndObject();
            } else {
                this.authenticatedUser(generator, nickName, avatarUrl, openId, userDescriptor);
            }
        }
    }

    private void authenticatedUser(JsonGenerator generator, String nickName, String avatarUrl, String unionId, UserDescriptor userDescriptor) throws IOException {
        generator.writeStartObject();
        generator.writeStringField("token", "byShortcut");
        generator.writeObjectFieldStart("user");
        generator.writeStringField("id", userDescriptor.id());
        generator.writeStringField("bindId", unionId);
        generator.writeStringField("username", userDescriptor.username());
        generator.writeBooleanField("available", userDescriptor.isAvailable());
        generator.writeStringField("nickName", nickName);
        generator.writeStringField("avatarUrl", avatarUrl);
        generator.writeEndObject();
        generator.writeEndObject();
    }
}
