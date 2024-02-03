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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author icodening
 * @date 2024.02.03
 */
public abstract class VirtualThreadExecutorServiceProvider {

    private VirtualThreadExecutorServiceProvider() {
    }

    public static ExecutorService tryInstantiateVitrualThreadExecutorService() {
        return initVirtualThreadExecutorService();
    }

    private static ExecutorService initVirtualThreadExecutorService() {
        try {
            return (ExecutorService) Executors.class
                    .getDeclaredMethod("newVirtualThreadPerTaskExecutor")
                    .invoke(null);
        } catch (Throwable ignored) {
            return null;
        }
    }
}
