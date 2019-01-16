/*
 *  Copyright 2018 www.foxtail.cc All rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */
package identity.foxtail.web;

import cc.foxtail.util.NumberHelper;
import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import identity.foxtail.core.application.UserApplicationService;
import identity.foxtail.core.application.command.RegisterUserCommand;
import identity.foxtail.core.domain.model.id.UserDescriptor;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/***
 * @author <a href="www.foxtail.cc/authors/guan xianghuang">guan xiangHuan</a>
 * @since JDK8.0
 * @version 0.0.1 20180408
 */
@WebServlet(urlPatterns = {"/v1/users/*"}, name = "users", asyncSupported = false)
public class UserServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        if (pathInfo != null) {

        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        boolean authentica = NumberHelper.booleanOf((String) session.getAttribute("authentica"));
        if (authentica) {
            JsonFactory jasonFactory = new JsonFactory();
            JsonParser parser = jasonFactory.createParser(request.getInputStream());
            String username = null;
            String cellphoneNumber = null;
            String password = null;
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
                }
            }
            RegisterUserCommand registerUserCommand = new RegisterUserCommand(username, password, cellphoneNumber);
            UserApplicationService userApplicationService = new UserApplicationService();
            UserDescriptor userDescriptor = userApplicationService.registerUser(registerUserCommand);
            response.setContentType("application/json; charset=UTF-8");
            JsonGenerator generator = jasonFactory.createGenerator(response.getOutputStream(), JsonEncoding.UTF8)
                    .setPrettyPrinter(new DefaultPrettyPrinter());
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
