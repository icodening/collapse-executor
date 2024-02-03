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
package com.icodening.collapse.spring.boot.autoconfigure.aop;

import com.icodening.collapse.aop.CollapseMethodInterceptor;
import com.icodening.collapse.aop.CollapsibleAnnotationAspect;
import com.icodening.collapse.core.support.BlockingCallableGroupCollapseExecutor;
import com.icodening.collapse.spring.boot.autoconfigure.ConditionalOnCollapseEnabled;
import org.aopalliance.intercept.MethodInterceptor;
import org.aspectj.lang.JoinPoint;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author icodening
 * @date 2023.07.07
 */
@ConditionalOnCollapseEnabled
@Configuration(proxyBeanMethods = false)
public class CollapseAopAutoConfiguration {

    @ConditionalOnClass({JoinPoint.class, CollapsibleAnnotationAspect.class})
    static class AspectAutoConfiguration {

        @Bean
        public CollapsibleAnnotationAspect collapsibleAnnotationAspect(BlockingCallableGroupCollapseExecutor collapseExecutor) {
            return new CollapsibleAnnotationAspect(collapseExecutor);
        }
    }

    @ConditionalOnClass({MethodInterceptor.class, CollapseMethodInterceptor.class})
    static class AopallianceAutoConfiguration {

        @Bean
        public CollapseMethodInterceptor collapseMethodInterceptor(BlockingCallableGroupCollapseExecutor collapseExecutor) {
            return new CollapseMethodInterceptor(collapseExecutor);
        }
    }
}
