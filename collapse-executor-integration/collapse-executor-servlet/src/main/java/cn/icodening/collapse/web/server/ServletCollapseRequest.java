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
package cn.icodening.collapse.web.server;

import cn.icodening.collapse.web.pattern.RequestCollapseGroup;

import javax.servlet.AsyncContext;
import java.util.Objects;

/**
 * @author icodening
 * @date 2023.05.22
 */
public class ServletCollapseRequest {

    private final RequestCollapseGroup groupKey;

    private final AsyncContext asyncContext;

    public ServletCollapseRequest(RequestCollapseGroup groupKey, AsyncContext asyncContext) {
        this.groupKey = groupKey;
        this.asyncContext = asyncContext;
    }

    public RequestCollapseGroup getGroupKey() {
        return groupKey;
    }

    public AsyncContext getAsyncContext() {
        return asyncContext;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServletCollapseRequest that = (ServletCollapseRequest) o;
        return groupKey.equals(that.groupKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(groupKey);
    }
}
