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

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter(filterName = "CaptchaFilter", urlPatterns = "/v1/user/*")
public class CaptchaFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        //Validate captchca if required
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                String name = cookie.getName();
                String value = cookie.getValue();
                if ("required_captcha".equals(name) && "true".equals(value)) {
                    String captch = null;
                    JsonFactory jasonFactory = new JsonFactory();
                    JsonParser jsonParser = jasonFactory.createParser(request.getInputStream());
                    while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
                        String fieldname = jsonParser.getCurrentName();
                        switch (fieldname) {
                            case "captcha":
                                jsonParser.nextValue();
                                captch = jsonParser.getValueAsString();
                                break;
                        }
                    }
                }
            }
            //验证码不正确直接返回
            //正确清楚
            Cookie cookie = new Cookie("required_captcha", "true");
            cookie.setMaxAge(0);
            response.addCookie(cookie);
        }
        chain.doFilter(request, servletResponse);
    }

    @Override
    public void destroy() {

    }
}
