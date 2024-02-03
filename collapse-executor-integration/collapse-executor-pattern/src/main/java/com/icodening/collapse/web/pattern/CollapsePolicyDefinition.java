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

import java.util.HashSet;
import java.util.Set;

/**
 * @author icodening
 * @date 2023.06.29
 */
public class CollapsePolicyDefinition {

    public static final CollapsePolicyDefinition DEFAULT_POLICY = new CollapsePolicyDefinition();

    private Set<String> collapseRequestHeaders = new HashSet<>();

    private Set<String> collapseRequestQueries = new HashSet<>();

    public Set<String> getCollapseRequestHeaders() {
        return collapseRequestHeaders;
    }

    public void setCollapseRequestHeaders(Set<String> collapseRequestHeaders) {
        this.collapseRequestHeaders = collapseRequestHeaders;
    }

    public Set<String> getCollapseRequestQueries() {
        return collapseRequestQueries;
    }

    public void setCollapseRequestQueries(Set<String> collapseRequestQueries) {
        this.collapseRequestQueries = collapseRequestQueries;
    }
}
