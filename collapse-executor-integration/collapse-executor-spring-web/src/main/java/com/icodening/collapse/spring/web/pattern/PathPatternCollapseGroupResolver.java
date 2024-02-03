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
package com.icodening.collapse.spring.web.pattern;

import com.icodening.collapse.web.pattern.AbstractConfigurationCollapseGroupResolver;
import com.icodening.collapse.web.pattern.CollapseDefinitionProperties;
import com.icodening.collapse.web.pattern.RequestAttributes;
import org.springframework.http.server.PathContainer;
import org.springframework.web.util.pattern.PathPatternParser;

/**
 * @author icodening
 * @date 2023.07.15
 * @see PathPatternParser
 */
public class PathPatternCollapseGroupResolver extends AbstractConfigurationCollapseGroupResolver {

    private static final PathPatternParser PATTERN_PARSER = PathPatternParser.defaultInstance;

    public PathPatternCollapseGroupResolver() {
        super();
    }

    public PathPatternCollapseGroupResolver(CollapseDefinitionProperties collapseDefinitionProperties) {
        super(collapseDefinitionProperties);
    }

    @Override
    protected boolean matches(RequestAttributes request, String pattern) {
        String path = request.getURI().getPath();
        return PATTERN_PARSER.parse(pattern)
                .matches(PathContainer.parsePath(path));
    }
}
