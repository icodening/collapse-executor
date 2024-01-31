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
package cn.icodening.collapse.web.pattern;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Logger;

/**
 * @author icodening
 * @date 2023.06.29
 */
public abstract class AbstractConfigurationCollapseGroupResolver implements CollapseGroupResolver {

    private static final Logger LOGGER = Logger.getLogger(AbstractConfigurationCollapseGroupResolver.class.getName());

    private CollapseDefinitionProperties collapseDefinitionProperties;

    public AbstractConfigurationCollapseGroupResolver() {

    }

    public AbstractConfigurationCollapseGroupResolver(CollapseDefinitionProperties collapseDefinitionProperties) {
        this.collapseDefinitionProperties = Objects.requireNonNull(collapseDefinitionProperties, "collapseDefinitionProperties must be not null.");
    }

    public void setCollapseDefinitionProperties(CollapseDefinitionProperties collapseDefinitionProperties) {
        this.collapseDefinitionProperties = collapseDefinitionProperties;
    }

    public CollapseDefinitionProperties getCollapseDefinitionProperties() {
        return collapseDefinitionProperties;
    }

    @Override
    public RequestCollapseGroup resolve(RequestAttributes requestAttributes) {
        CollapseDefinitionProperties collapseDefinitionProperties = Objects.requireNonNull(this.collapseDefinitionProperties, "collapseDefinitionProperties must be not null.");
        if (!collapseDefinitionProperties.isEnabled()) {
            return null;
        }
        for (CollapseGroupDefinition definition : collapseDefinitionProperties.getCollapseGroups()) {
            Set<String> uris = definition.getPatterns();
            for (String uri : uris) {
                if (matches(requestAttributes, uri)) {
                    String groupPolicyName = definition.getCollapsePolicyName();
                    CollapsePolicyDefinition collapsePolicy = findCollapsePolicyDefinition(groupPolicyName);
                    if (collapsePolicy == null) {
                        LOGGER.fine(String.format("['%s' not found.]", groupPolicyName));
                        return null;
                    }
                    RequestCollapseGroup requestCollapseGroup = new RequestCollapseGroup();
                    //1. method
                    requestCollapseGroup.setMethod(requestAttributes.getMethod());
                    URI requestURI = requestAttributes.getURI();
                    //2. path
                    requestCollapseGroup.setPath(requestURI.getPath());
                    //3. header
                    for (String headerName : collapsePolicy.getCollapseRequestHeaders()) {
                        List<String> header = requestAttributes.getHeaders().get(headerName);
                        if (!(header == null || header.isEmpty())) {
                            requestCollapseGroup.getHeaders().put(headerName, header);
                        }
                    }
                    //4.query
                    Map<String, List<String>> queryMap = queryStringToMap(requestURI.getQuery());
                    for (String collapseRequestQuery : collapsePolicy.getCollapseRequestQueries()) {
                        List<String> value = queryMap.get(collapseRequestQuery);
                        if (!(value == null || value.isEmpty())) {
                            requestCollapseGroup.getQueries().put(collapseRequestQuery, value);
                        }
                    }
                    return requestCollapseGroup;
                }
            }
        }
        return null;
    }

    private CollapsePolicyDefinition findCollapsePolicyDefinition(String groupPolicyName) {
        if (groupPolicyName == null || groupPolicyName.trim().isEmpty()) {
            return CollapsePolicyDefinition.DEFAULT_POLICY;
        }
        return this.collapseDefinitionProperties.getCollapsePolicies().get(groupPolicyName);
    }

    private Map<String, List<String>> queryStringToMap(String queryString) {
        if (queryString == null || queryString.isEmpty()) {
            return Collections.emptyMap();
        }
        String[] queryKVs = queryString.split("&");
        Map<String, List<String>> queryMap = new HashMap<>();
        for (String queryKV : queryKVs) {
            int idx = queryKV.indexOf("=");
            if (idx == -1) {
                continue;
            }
            String key = queryKV.substring(0, idx);
            String value = queryKV.substring(idx + 1);
            queryMap.computeIfAbsent(key, (k) -> new ArrayList<>()).add(value);
        }
        return queryMap;
    }

    protected abstract boolean matches(RequestAttributes request, String pattern);

}
