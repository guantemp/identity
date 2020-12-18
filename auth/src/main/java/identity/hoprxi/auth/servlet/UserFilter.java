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
import identity.hoprxi.core.domain.servers.PasswordService;
import salt.hoprxi.utils.NumberHelper;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/***
 * @author <a href="www.hoprxi.com/authors/guan xianghuang">guan xiangHuan</a>
 * @since JDK8.0
 * @version 0.0.1 2018-05-10
 */
@WebFilter(filterName = "UserFilter", urlPatterns = "/v1/users/*")
public class UserFilter implements Filter {
    public void destroy() {
    }

    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws ServletException, IOException {
        HttpServletRequest request = (HttpServletRequest) req;
        String method = request.getMethod();
        if (method.equals("POST")) {
            JsonFactory jasonFactory = new JsonFactory();
            JsonParser parser = jasonFactory.createParser(request.getInputStream());
            String command = null;
            String username = null;
            String cellphoneNumber = null;
            String password = null;
            String confirmPassword = null;
            while (parser.nextToken() != JsonToken.END_OBJECT) {
                String fieldname = parser.getCurrentName();
                switch (fieldname) {
                    case "username":
                        parser.nextValue();
                        username = parser.getValueAsString();
                        break;
                    case "cellphoneNumber":
                        parser.nextValue();
                        cellphoneNumber = parser.getValueAsString();
                        break;
                    case "password":
                        parser.nextValue();
                        password = parser.getValueAsString();
                        break;
                    case "confirmPassword":
                        parser.nextValue();
                        confirmPassword = parser.getValueAsString();
                }
            }
            HttpSession session = request.getSession(true);
            int step = NumberHelper.intOf((String) session.getAttribute("step"), 0);
            HttpServletResponse response = (HttpServletResponse) resp;
            response.setContentType("application/json; charset=UTF-8");
            JsonGenerator generator = jasonFactory.createGenerator(response.getOutputStream(), JsonEncoding.UTF8)
                    .setPrettyPrinter(new DefaultPrettyPrinter());
            switch (command) {
                case "validCellphoneNumberCommand":
                    generator.writeStartObject();
                    generator.writeStringField("error", "400");
                    generator.writeStringField("msg", cellphoneNumber + " is exist");
                    generator.writeEndObject();
                    session.setAttribute("step", step + 1);
                    break;
                case "validUsernameCommand":
                    generator.writeStartObject();
                    generator.writeStringField("error", "400");
                    generator.writeStringField("msg", username + " is exist");
                    session.setAttribute("step", step + 1);
                    break;
                case "confrimPassCommand":
                    if (!validPassword(password, confirmPassword)) {
                        generator.writeStartObject();
                        generator.writeStringField("error", "400");
                        generator.writeStringField("msg", "Password is mismatch");
                        generator.writeEndObject();
                    } else {
                        session.setAttribute("step", step);
                    }
                    break;
                default:
                    step = NumberHelper.intOf((String) session.getAttribute("step"), 0);
                    if (step >= 3) {
                        session.removeAttribute("step");
                        session.setAttribute("authentica", true);
                    }
            }
            generator.flush();
            generator.close();
        }
        chain.doFilter(req, resp);
    }

    private boolean validPassword(String password, String confrimPass) {
        if (password == null || confrimPass == null || password.length() < 6 || confrimPass.length() < 6
                || password.length() > 40 || confrimPass.length() > 40 || !password.equals(confrimPass))
            return false;
        PasswordService ps = new PasswordService();
        if (ps.isWeak(password))
            return false;
        return true;
    }

    public void init(FilterConfig config) throws ServletException {

    }

}
