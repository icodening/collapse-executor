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
package com.icodening.collapse.web.pattern;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author icodening
 * @date 2023.06.29
 */
public class CollapseDefinitionProperties {

    private boolean enabled = false;

    private Map<String, CollapsePolicyDefinition> collapsePolicies = new LinkedHashMap<>();

    private List<CollapseGroupDefinition> collapseGroups = new ArrayList<>();

    public CollapseDefinitionProperties() {
        this.collapsePolicies.put("*", CollapsePolicyDefinition.DEFAULT_POLICY);
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Map<String, CollapsePolicyDefinition> getCollapsePolicies() {
        return collapsePolicies;
    }

    public void setCollapsePolicies(Map<String, CollapsePolicyDefinition> collapsePolicies) {
        this.collapsePolicies = collapsePolicies;
    }

    public List<CollapseGroupDefinition> getCollapseGroups() {
        return collapseGroups;
    }

    public void setCollapseGroups(List<CollapseGroupDefinition> collapseGroups) {
        this.collapseGroups = collapseGroups;
    }
}
