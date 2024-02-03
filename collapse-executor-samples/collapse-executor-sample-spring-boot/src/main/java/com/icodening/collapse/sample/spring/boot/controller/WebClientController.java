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
import com.icodening.collapse.spring.web.client.reactive.CollapseExchangeFilterFunction;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

/**
 * @author icodening
 * @date 2023.06.30
 */
@RestController
@RequestMapping("/webclient")
public class WebClientController {

    private final WebClient webClientWithCollapse;

    private final WebClient webClientWithoutCollapse;

    public WebClientController(CollapseExchangeFilterFunction exchangeFilterFunction,
                               @Value("${server.port}") int port) {
        UriComponentsBuilder baseUrl = UriComponentsBuilder.newInstance()
                .scheme("http")
                .host("localhost")
                .port(port);
        DefaultUriBuilderFactory defaultUriBuilderFactory = new DefaultUriBuilderFactory(baseUrl);
        this.webClientWithCollapse = WebClient.builder().uriBuilderFactory(defaultUriBuilderFactory).filter(exchangeFilterFunction).build();
        this.webClientWithoutCollapse = WebClient.builder().uriBuilderFactory(defaultUriBuilderFactory).build();
    }

    @RequestMapping(value = "/collapse/noop0")
    public Mono<UserEntity> noop0WithCollapse() {
        return this.webClientWithCollapse.get()
                .uri("/test/noop0")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(UserEntity.class);
    }

    @RequestMapping(value = "/collapse/noop1")
    public Mono<UserEntity> noop1WithCollapse() {
        return this.webClientWithCollapse.get()
                .uri("/test/noop1")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(UserEntity.class);
    }

    @RequestMapping(value = "/collapse/noop100")
    public Mono<UserEntity> noop100WithCollapse() {
        return this.webClientWithCollapse.get()
                .uri("/test/noop100")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(UserEntity.class);
    }

    @RequestMapping(value = "/without/noop0")
    public Mono<UserEntity> noop0WithoutCollapse() {
        return this.webClientWithoutCollapse.get()
                .uri("/test/noop0")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(UserEntity.class);
    }

    @RequestMapping(value = "/without/noop1")
    public Mono<UserEntity> noop1WithoutCollapse() {
        return this.webClientWithoutCollapse.get()
                .uri("/test/noop1")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(UserEntity.class);
    }

    @RequestMapping(value = "/without/noop100")
    public Mono<UserEntity> noop100WithoutCollapse() {
        return this.webClientWithoutCollapse.get()
                .uri("/test/noop100")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(UserEntity.class);
    }
}
