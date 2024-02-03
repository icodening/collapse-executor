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
package com.icodening.collapse.core.support;

import com.icodening.collapse.core.AsyncSameOutputCollapseExecutor;
import com.icodening.collapse.core.Input;
import com.icodening.collapse.core.ListeningCollector;
import com.icodening.collapse.core.util.ThrowableCallable;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * @author icodening
 * @date 2023.05.21
 */
public class AsyncCallableGroupCollapseExecutor {

    private final InternalSameOutputCollapseExecutorAsyncCallableGroupCollapseExecutor<Object> collapseExecutor;

    public AsyncCallableGroupCollapseExecutor() {
        this.collapseExecutor = new InternalSameOutputCollapseExecutorAsyncCallableGroupCollapseExecutor<>();
        this.collapseExecutor.setName(this.getClass().getSimpleName());
    }

    public AsyncCallableGroupCollapseExecutor(ListeningCollector collector) {
        this.collapseExecutor = new InternalSameOutputCollapseExecutorAsyncCallableGroupCollapseExecutor<>(collector);
        this.collapseExecutor.setName(this.getClass().getSimpleName());
    }

    public void setExecutor(Executor executor) {
        this.collapseExecutor.setExecutor(executor);
    }

    public void setName(String name) {
        this.collapseExecutor.setName(name);
    }

    @SuppressWarnings("unchecked")
    public <R> CompletableFuture<R> execute(Object group, ThrowableCallable<R> callable) {
        CallableGroup<R> callableGroup = new CallableGroup<>(group, callable);
        return (CompletableFuture<R>) collapseExecutor.execute((CallableGroup<Object>) callableGroup);
    }

    private static class InternalSameOutputCollapseExecutorAsyncCallableGroupCollapseExecutor<R> extends AsyncSameOutputCollapseExecutor<CallableGroup<R>, R> {

        public InternalSameOutputCollapseExecutorAsyncCallableGroupCollapseExecutor() {
            super();
        }

        private InternalSameOutputCollapseExecutorAsyncCallableGroupCollapseExecutor(ListeningCollector collector) {
            super(collector);
        }

        @Override
        protected R doExecute(Collection<Input<CallableGroup<R>>> inputs) throws Throwable {
            return inputs.iterator().next().value().getCallable().call();
        }
    }
}
