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
import java.util.regex.Pattern;

/**
 * @author icodening
 * @date 2023.07.16
 */
public class RegexConfigurationCollapseGroupResolver extends AbstractConfigurationCollapseGroupResolver {

    public RegexConfigurationCollapseGroupResolver() {
    }

    public RegexConfigurationCollapseGroupResolver(CollapseDefinitionProperties collapseDefinitionProperties) {
        super(collapseDefinitionProperties);
    }

    @Override
    protected boolean matches(RequestAttributes request, String pattern) {
        URI uri = request.getURI();
        String path = uri.getPath();
        return Pattern.matches(pattern, path);
    }
}
