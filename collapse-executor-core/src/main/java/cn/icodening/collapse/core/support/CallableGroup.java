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
package cn.icodening.collapse.core.support;

import cn.icodening.collapse.core.util.ThrowableCallable;

import java.util.Objects;

/**
 * @author icodening
 * @date 2023.05.22
 */
class CallableGroup<R> {

    private final Object group;

    private final ThrowableCallable<R> callable;

    CallableGroup(Object group, ThrowableCallable<R> callable) {
        this.group = group;
        this.callable = callable;
    }

    public Object getGroup() {
        return group;
    }

    public ThrowableCallable<R> getCallable() {
        return callable;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CallableGroup<?> that = (CallableGroup<?>) o;
        return Objects.equals(group, that.group);
    }

    @Override
    public int hashCode() {
        return Objects.hash(group);
    }
}
