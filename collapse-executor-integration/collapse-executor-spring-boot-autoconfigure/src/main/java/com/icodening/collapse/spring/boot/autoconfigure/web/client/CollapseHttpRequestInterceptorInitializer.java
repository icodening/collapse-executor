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
package com.icodening.collapse.spring.boot.autoconfigure.web.client;

import com.icodening.collapse.spring.web.client.CollapseHttpRequestInterceptorConfigurator;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ApplicationObjectSupport;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * @author icodening
 * @date 2023.10.26
 */
public class CollapseHttpRequestInterceptorInitializer extends ApplicationObjectSupport implements SmartInitializingSingleton {

    private static final Logger LOGGER = Logger.getLogger(CollapseHttpRequestInterceptorInitializer.class.getName());

    private final CollapseHttpRequestInterceptorConfigurator collapseHttpRequestInterceptorConfigurator;

    private List<String> restTemplateBeanNames = Collections.emptyList();

    public CollapseHttpRequestInterceptorInitializer(CollapseHttpRequestInterceptorConfigurator collapseHttpRequestInterceptorConfigurator) {
        this.collapseHttpRequestInterceptorConfigurator = collapseHttpRequestInterceptorConfigurator;
    }

    public void setCandidateRestTemplateBeanNames(List<String> restTemplateBeanNames) {
        this.restTemplateBeanNames = restTemplateBeanNames;
    }

    @Override
    public void afterSingletonsInstantiated() {
        ApplicationContext applicationContext = obtainApplicationContext();
        Map<String, RestTemplate> restTemplates = applicationContext.getBeansOfType(RestTemplate.class);
        LOGGER.info("Found RestTemplate bean names. " + Arrays.toString(restTemplates.keySet().toArray(new String[0])));
        for (String restTemplateBeanName : restTemplateBeanNames) {
            RestTemplate restTemplate = restTemplates.get(restTemplateBeanName);
            if (restTemplate == null) {
                LOGGER.warning(String.format("[%s] RestTemplate not exists, will be ignore initialization.", restTemplateBeanName));
                continue;
            }
            collapseHttpRequestInterceptorConfigurator.configurer(restTemplate);
            LOGGER.info(String.format("RestTemplate [%s] configurer completed.", restTemplateBeanName));
        }
    }
}
