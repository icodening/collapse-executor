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
package cn.icodening.collapse.sample.spring.boot.service;

import cn.icodening.collapse.sample.spring.boot.entity.UserEntity;
import cn.icodening.collapse.sample.spring.boot.executor.WebClientBatchGetExecutor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * @author icodening
 * @date 2023.06.24
 */
@Component
public class WebClientCollapseBlockingCallSample extends AbstractBlockingCallSample {

    private final WebClient webClient;

    private final WebClientBatchGetExecutor webClientBatchGetExecutor;

    public WebClientCollapseBlockingCallSample(WebClient webClient,
                                               WebClientBatchGetExecutor webClientBatchGetExecutor) {
        super(WebClient.class.getSimpleName());
        this.webClient = webClient;
        this.webClientBatchGetExecutor = webClientBatchGetExecutor;
    }

    @Override
    protected UserEntity doBatchQuery(long id) throws Throwable {
        return webClientBatchGetExecutor.execute(id);
    }

    @Override
    protected UserEntity doSingleQuery(UriComponentsBuilder baseUri, long id) {
        return webClient.get()
                .uri(baseUri.path("/user/" + id).build().toUri())
                .retrieve()
                .bodyToMono(UserEntity.class)
                .block();
    }
}
