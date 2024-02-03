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
package com.icodening.collapse.core;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @author icodening
 * @date 2023.05.21
 */
public abstract class AsyncSameOutputCollapseExecutor<INPUT, OUTPUT> extends CollapseExecutorAsyncSupport<INPUT, OUTPUT, OUTPUT> {

    public AsyncSameOutputCollapseExecutor() {
        super();
    }

    public AsyncSameOutputCollapseExecutor(ListeningCollector collector) {
        super(collector);
    }

    @Override
    protected void bindingOutput(OUTPUT batchOutput, List<Bundle<INPUT, CompletableFuture<OUTPUT>>> bundles) {
        for (Bundle<INPUT, CompletableFuture<OUTPUT>> bundle : bundles) {
            bundle.bindOutput(CompletableFuture.completedFuture(batchOutput));
        }
    }
}
