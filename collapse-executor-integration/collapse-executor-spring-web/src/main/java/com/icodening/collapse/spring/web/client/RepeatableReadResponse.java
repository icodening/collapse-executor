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

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * @author icodening
 * @date 2023.05.16
 */
class RepeatableReadResponse implements ClientHttpResponse {

    private final int rawStatusCode;

    private final HttpStatus statusCode;

    private final String statusText;

    private final HttpHeaders headers;

    private final byte[] body;

    RepeatableReadResponse(int rawStatusCode, HttpStatus statusCode, String statusText, HttpHeaders headers, byte[] body) {
        this.rawStatusCode = rawStatusCode;
        this.statusCode = statusCode;
        this.statusText = statusText;
        this.headers = headers;
        this.body = body;
    }

    @Override
    public void close() {
        //No op
    }

    @Override
    public int getRawStatusCode() {
        return rawStatusCode;
    }

    @Override
    public HttpStatus getStatusCode() {
        return statusCode;
    }

    @Override
    public String getStatusText() {
        return statusText;
    }

    @Override
    public HttpHeaders getHeaders() {
        return headers;
    }

    @Override
    public InputStream getBody() {
        return new ByteArrayInputStream(body);
    }
}