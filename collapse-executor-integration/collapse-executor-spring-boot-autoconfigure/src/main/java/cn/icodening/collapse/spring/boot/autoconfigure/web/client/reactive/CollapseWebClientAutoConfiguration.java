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
package cn.icodening.collapse.spring.boot.autoconfigure.web.client.reactive;

import cn.icodening.collapse.core.ListeningCollector;
import cn.icodening.collapse.core.support.FutureCallableGroupCollapseExecutor;
import cn.icodening.collapse.spring.boot.autoconfigure.ConditionalOnCollapseEnabled;
import cn.icodening.collapse.spring.web.client.reactive.CollapseExchangeFilterFunction;
import cn.icodening.collapse.spring.web.pattern.PathPatternCollapseGroupResolver;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * @author icodening
 * @date 2023.06.23
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnCollapseEnabled
@ConditionalOnClass(WebClient.class)
public class CollapseWebClientAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(CollapseExchangeFilterFunction.class)
    public CollapseExchangeFilterFunction collapseExchangeFilterFunction(ListeningCollector listeningCollector,
                                                                         CollapseWebClientProperties collapseWebClientProperties) {
        CollapseExchangeFilterFunction collapseExchangeFilterFunction = new CollapseExchangeFilterFunction();
        collapseExchangeFilterFunction.setCollapseGroupResolver(new PathPatternCollapseGroupResolver(collapseWebClientProperties));
        collapseExchangeFilterFunction.setFutureCallableGroupCollapseExecutor(new FutureCallableGroupCollapseExecutor(listeningCollector));
        return collapseExchangeFilterFunction;
    }

}
