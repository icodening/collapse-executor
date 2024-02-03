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

import com.icodening.collapse.core.ListeningCollector;
import com.icodening.collapse.core.support.BlockingCallableGroupCollapseExecutor;
import com.icodening.collapse.spring.boot.autoconfigure.ConditionalOnCollapseEnabled;
import com.icodening.collapse.spring.web.client.CollapseHttpRequestInterceptor;
import com.icodening.collapse.spring.web.client.CollapseHttpRequestInterceptorConfigurator;
import com.icodening.collapse.spring.web.pattern.PathPatternCollapseGroupResolver;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * @author icodening
 * @date 2023.05.25
 */
@ConditionalOnClass(RestTemplate.class)
@ConditionalOnCollapseEnabled
@Configuration(proxyBeanMethods = false)
public class CollapseRestTemplateAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(CollapseHttpRequestInterceptor.class)
    public CollapseHttpRequestInterceptor collapseHttpRequestInterceptor(ListeningCollector listeningCollector,
                                                                         CollapseRestTemplateProperties collapseRestTemplateProperties) {
        CollapseHttpRequestInterceptor collapseHttpRequestInterceptor = new CollapseHttpRequestInterceptor();
        collapseHttpRequestInterceptor.setCollapseGroupResolver(new PathPatternCollapseGroupResolver(collapseRestTemplateProperties));
        collapseHttpRequestInterceptor.setBlockingCallableGroupCollapseExecutor(new BlockingCallableGroupCollapseExecutor(listeningCollector));
        return collapseHttpRequestInterceptor;
    }

    @Bean
    @ConditionalOnBean(CollapseHttpRequestInterceptor.class)
    public CollapseHttpRequestInterceptorConfigurator collapseHttpRequestInterceptorConfigurer(CollapseHttpRequestInterceptor collapseHttpRequestInterceptor) {
        return new CollapseHttpRequestInterceptorConfigurator(collapseHttpRequestInterceptor);
    }

    @Bean
    @ConditionalOnBean(CollapseHttpRequestInterceptorConfigurator.class)
    public CollapseHttpRequestInterceptorInitializer collapseHttpRequestInterceptorInitializer(CollapseRestTemplateProperties collapseRestTemplateProperties,
                                                                                               CollapseHttpRequestInterceptorConfigurator configurator) {
        List<String> applyBeans = collapseRestTemplateProperties.getApplyBeanNames();
        CollapseHttpRequestInterceptorInitializer initializer = new CollapseHttpRequestInterceptorInitializer(configurator);
        initializer.setCandidateRestTemplateBeanNames(applyBeans);
        return initializer;
    }

}

