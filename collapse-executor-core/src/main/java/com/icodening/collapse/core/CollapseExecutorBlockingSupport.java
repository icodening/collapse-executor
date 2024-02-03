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

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

/**
 * @author icodening
 * @date 2023.05.23
 */
public abstract class CollapseExecutorBlockingSupport<INPUT, OUTPUT, BATCH_OUTPUT> extends AbstractCollapseExecutor<INPUT, OUTPUT, BATCH_OUTPUT> {

    public CollapseExecutorBlockingSupport() {
        super();
    }

    public CollapseExecutorBlockingSupport(ListeningCollector collector) {
        super(collector);
    }

    @Override
    protected final OUTPUT returning(Bundle<INPUT, OUTPUT> bundle) throws Throwable {
        ThreadlessExecutor threadlessExecutor = (ThreadlessExecutor) bundle.getCallbackExecutor();
        CompletableFuture<OUTPUT> completableFuture = bundle.getListeningResult();
        try {
            while (!completableFuture.isDone()) {
                threadlessExecutor.waitAndDrain();
            }
            return completableFuture.get();
        } catch (ExecutionException executionException) {
            //throw actual exception
            throw executionException.getCause();
        } finally {
            threadlessExecutor.shutdown();
        }
    }

    @Override
    protected final Executor getCallbackExecutor() {
        return new ThreadlessExecutor();
    }
}
