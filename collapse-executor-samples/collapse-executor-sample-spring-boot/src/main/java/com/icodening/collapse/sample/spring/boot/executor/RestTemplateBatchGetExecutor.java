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
package com.icodening.collapse.sample.spring.boot.executor;

import com.icodening.collapse.core.ListeningCollector;
import com.icodening.collapse.sample.spring.boot.entity.UserEntity;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.List;

/**
 * @author icodening
 * @date 2023.05.17
 */
@Component
public class RestTemplateBatchGetExecutor extends AbstractBatchGetExecutor {

    private final RestTemplate restTemplate;

    public RestTemplateBatchGetExecutor(ListeningCollector collector) {
        super(collector);
        this.restTemplate = new RestTemplate();
    }

    @Override
    protected List<UserEntity> doBatchGet(URI uri) {
        return restTemplate.exchange(uri, HttpMethod.GET, null, new ParameterizedTypeReference<List<UserEntity>>() {
        }).getBody();
    }
}
