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
@WebServlet(urlPatterns = {"/v1/auth"}, name = "auth", asyncSupported = false, initParams = {
        @WebInitParam(name = "appid", value = ""),
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
        String js_code = req.getParameter("js_code");
        JsonFactory jasonFactory = new JsonFactory();
        resp.setContentType("application/json; charset=UTF-8");
        JsonGenerator generator = jasonFactory.createGenerator(resp.getOutputStream(), JsonEncoding.UTF8)
                .setPrettyPrinter(new DefaultPrettyPrinter());
        if (js_code == null || js_code.isEmpty()) {
            generator.writeStartObject();
            generator.writeNumberField("code", 400);
            generator.writeStringField("msg", "非法凭证号：" + js_code);
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
                        ok(generator, jsonResult);
                    }
                }
            } catch (URISyntaxException e) {
                generator.writeStartObject();
                generator.writeNumberField("code", 401);
                generator.writeStringField("msg", "微信服务器离线!");
                generator.writeEndObject();
            }
        }
        generator.flush();
        generator.close();
    }

    private void ok(JsonGenerator generator, String json) throws IOException {
        String openid = null;
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
                        openid = parser.getValueAsString();
                        break;
                    case "session_key":
                        session_key = parser.getValueAsString();
                        break;
                    case "unionId":
                        unionId = parser.getValueAsString();
                        break;
                }
            }
        }
        if (unionId != null) //替换openid
            openid = unionId;
        UserApplicationService service = new UserApplicationService();
        UserDescriptor userDescriptor = service.authenticateByThirdParty(openid);
        if (userDescriptor == UserDescriptor.NullUserDescriptor) {

        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }

}
