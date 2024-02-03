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
package com.icodening.collapse.core.util;

import java.util.function.Supplier;

/**
 * @author icodening
 * @date 2023.05.22
 */
public class CacheableSupplier<T> implements Supplier<T> {

    private final Supplier<T> supplier;

    private volatile T object;

    private volatile boolean initialized = false;

    private CacheableSupplier(Supplier<T> supplier) {
        this.supplier = supplier;
    }

    public static <T> Supplier<T> from(Supplier<T> supplier) {
        return new CacheableSupplier<>(supplier);
    }

    @Override
    public T get() {
        if (initialized) {
            return object;
        }
        synchronized (this) {
            if (!initialized) {
                this.object = supplier.get();
                this.initialized = true;
            }
        }
        return object;
    }
}
