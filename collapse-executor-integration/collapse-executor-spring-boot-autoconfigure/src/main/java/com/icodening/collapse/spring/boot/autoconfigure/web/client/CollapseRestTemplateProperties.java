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

import com.icodening.collapse.web.pattern.CollapseDefinitionProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * @author icodening
 * @date 2023.06.29
 */
@ConfigurationProperties(prefix = "collapse.executor.rest-template")
public class CollapseRestTemplateProperties extends CollapseDefinitionProperties {

    /**
     * Specify the RestTemplate bean that needs to take effect
     */
    private List<String> applyBeanNames = new ArrayList<>();

    public List<String> getApplyBeanNames() {
        return applyBeanNames;
    }

    public void setApplyBeanNames(List<String> applyBeanNames) {
        this.applyBeanNames = applyBeanNames;
    }
}
