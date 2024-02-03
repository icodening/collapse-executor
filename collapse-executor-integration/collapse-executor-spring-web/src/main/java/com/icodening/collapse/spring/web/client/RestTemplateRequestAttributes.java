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
package com.icodening.collapse.spring.web.client;

import com.icodening.collapse.web.pattern.RequestAttributes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.util.Assert;

import java.net.URI;

/**
 * @author icodening
 * @date 2023.06.29
 */
public class RestTemplateRequestAttributes implements RequestAttributes {

    private final HttpRequest request;

    public RestTemplateRequestAttributes(HttpRequest request) {
        Assert.notNull(request, "request must be not null.");
        this.request = request;
    }

    @Override
    public String getMethod() {
        if (request.getMethod() == null) {
            return null;
        }
        return request.getMethod().name();
    }

    @Override
    public URI getURI() {
        return request.getURI();
    }

    @Override
    public HttpHeaders getHeaders() {
        return request.getHeaders();
    }
}
