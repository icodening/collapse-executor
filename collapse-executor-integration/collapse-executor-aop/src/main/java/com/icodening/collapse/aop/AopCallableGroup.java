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
package com.icodening.collapse.aop;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;

/**
 * @author icodening
 * @date 2023.07.07
 */
public class AopCallableGroup {

    private final Class<?> declaringClass;

    private final Method method;

    private final Object[] arguments;

    private final Object target;

    public AopCallableGroup(Method method, Object[] arguments, Object target) {
        this.declaringClass = method.getDeclaringClass();
        this.method = method;
        this.arguments = arguments;
        this.target = target;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AopCallableGroup that = (AopCallableGroup) o;
        return declaringClass.equals(that.declaringClass) && method.equals(that.method) && Arrays.equals(arguments, that.arguments) && target.equals(that.target);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(declaringClass, method, target);
        result = 31 * result + Arrays.hashCode(arguments);
        return result;
    }
}
