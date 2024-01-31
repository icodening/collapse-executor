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
package cn.icodening.collapse.spring.web.client;

import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author icodening
 * @date 2023.10.26
 */
public class CollapseHttpRequestInterceptorConfigurator {

    private final CollapseHttpRequestInterceptor collapseHttpRequestInterceptor;

    public CollapseHttpRequestInterceptorConfigurator(CollapseHttpRequestInterceptor collapseHttpRequestInterceptor) {
        Assert.notNull(collapseHttpRequestInterceptor, "collapseHttpRequestInterceptor must be not null.");
        this.collapseHttpRequestInterceptor = collapseHttpRequestInterceptor;
    }

    public void configurer(RestTemplate... restTemplate) {
        configurer(Arrays.asList(restTemplate));
    }

    public void configurer(List<RestTemplate> restTemplates) {
        doConfigurer(restTemplates, collapseHttpRequestInterceptor);
    }

    private void doConfigurer(List<RestTemplate> restTemplates,
                              CollapseHttpRequestInterceptor collapseHttpRequestInterceptor) {
        for (RestTemplate restTemplate : restTemplates) {
            List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>(restTemplate.getInterceptors());
            interceptors.add(collapseHttpRequestInterceptor);
            restTemplate.setInterceptors(interceptors);
        }
    }
}
