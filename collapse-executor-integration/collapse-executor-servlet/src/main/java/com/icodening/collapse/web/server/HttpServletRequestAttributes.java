/*
 * Copyright 2023-2024 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.icodening.collapse.web.server;

import com.icodening.collapse.web.pattern.RequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

/**
 * @author icodening
 * @date 2023.06.29
 */
public class HttpServletRequestAttributes implements RequestAttributes {

    private final String httpMethod;

    private final URI uri;

    private final Map<String, List<String>> httpHeaders;

    public HttpServletRequestAttributes(final HttpServletRequest httpServletRequest) {
        Objects.requireNonNull(httpServletRequest, "httpServletRequest must be not null.");
        this.httpMethod = httpServletRequest.getMethod();
        this.uri = createURI(httpServletRequest);
        this.httpHeaders = resolveHttpHeaders(httpServletRequest);
    }

    private URI createURI(HttpServletRequest httpServletRequest) {
        String url = httpServletRequest.getRequestURL().toString();
        String queryString = httpServletRequest.getQueryString();
        String query = queryString == null ? "" : "?" + queryString;
        String fullURI = url + query;
        return URI.create(fullURI);
    }

    @Override
    public String getMethod() {
        return httpMethod;
    }

    @Override
    public URI getURI() {
        return uri;
    }

    @Override
    public Map<String, List<String>> getHeaders() {
        return httpHeaders;
    }

    private static Map<String, List<String>> resolveHttpHeaders(HttpServletRequest httpServletRequest) {
        Map<String, List<String>> headers = new TreeMap<>(String::compareToIgnoreCase);
        Enumeration<String> headerNames = httpServletRequest.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            Enumeration<String> headerValues = httpServletRequest.getHeaders(headerName);
            while (headerValues.hasMoreElements()) {
                String headerValue = headerValues.nextElement();
                headers.computeIfAbsent(headerName, (key) -> new ArrayList<>()).add(headerValue);
            }
        }
        return headers;
    }
}
