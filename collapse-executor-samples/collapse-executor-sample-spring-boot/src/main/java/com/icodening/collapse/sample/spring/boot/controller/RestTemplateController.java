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
package com.icodening.collapse.sample.spring.boot.controller;

import com.icodening.collapse.sample.spring.boot.entity.UserEntity;
import com.icodening.collapse.spring.web.client.CollapseHttpRequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * @author icodening
 * @date 2023.06.30
 */
@RestController
@RequestMapping("/rest-template")
public class RestTemplateController {

    private final RestTemplate restTemplateWithCollapse;

    private final RestTemplate restTemplateWithoutCollapse;

    public RestTemplateController(CollapseHttpRequestInterceptor collapseHttpRequestInterceptor, @Value("${server.port}") int port) {
        UriComponentsBuilder baseUrl = UriComponentsBuilder.newInstance().scheme("http").host("localhost").port(port);
        this.restTemplateWithCollapse = new RestTemplateBuilder().rootUri(baseUrl.toUriString()).additionalInterceptors(collapseHttpRequestInterceptor).build();
        this.restTemplateWithoutCollapse = new RestTemplateBuilder().rootUri(baseUrl.toUriString()).build();
    }

    @RequestMapping(value = "/collapse/noop0")
    public UserEntity noop0WithCollapse() {
        return this.restTemplateWithCollapse.getForObject("/test/noop0", UserEntity.class);
    }

    @RequestMapping(value = "/collapse/noop1")
    public UserEntity noop1WithCollapse() {
        return this.restTemplateWithCollapse.getForObject("/test/noop1", UserEntity.class);
    }

    @RequestMapping(value = "/collapse/noop100")
    public UserEntity noop100WithCollapse() {
        return this.restTemplateWithCollapse.getForObject("/test/noop100", UserEntity.class);
    }

    @RequestMapping(value = "/without/noop0")
    public UserEntity noop0WithoutCollapse() {
        return this.restTemplateWithoutCollapse.getForObject("/test/noop0", UserEntity.class);
    }

    @RequestMapping(value = "/without/noop1")
    public UserEntity noop1WithoutCollapse() {
        return this.restTemplateWithoutCollapse.getForObject("/test/noop1", UserEntity.class);
    }

    @RequestMapping(value = "/without/noop100")
    public UserEntity noop100WithoutCollapse() {
        return this.restTemplateWithoutCollapse.getForObject("/test/noop100", UserEntity.class);
    }
}
