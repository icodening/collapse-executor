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
package cn.icodening.collapse.spring.web.client.reactive;

import cn.icodening.collapse.web.pattern.RequestAttributes;
import org.springframework.http.HttpHeaders;
import org.springframework.util.Assert;
import org.springframework.web.reactive.function.client.ClientRequest;

import java.net.URI;

/**
 * @author icodening
 * @date 2023.06.29
 */
public class WebClientRequestAttributes implements RequestAttributes {

    private final ClientRequest clientRequest;

    public WebClientRequestAttributes(ClientRequest clientRequest) {
        Assert.notNull(clientRequest, "clientRequest must be not null.");
        this.clientRequest = clientRequest;
    }

    @Override
    public String getMethod() {
        return clientRequest.method().name();
    }

    @Override
    public URI getURI() {
        return clientRequest.url();
    }

    @Override
    public HttpHeaders getHeaders() {
        return clientRequest.headers();
    }
}
