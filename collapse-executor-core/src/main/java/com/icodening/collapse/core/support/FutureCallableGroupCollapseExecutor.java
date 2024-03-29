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

import com.icodening.collapse.core.Bundle;
import com.icodening.collapse.core.CollapseExecutorAsyncSupport;
import com.icodening.collapse.core.Input;
import com.icodening.collapse.core.ListeningCollector;
import com.icodening.collapse.core.util.ThrowableCallable;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * @author icodening
 * @date 2023.06.09
 */
public class FutureCallableGroupCollapseExecutor {

    private static final Executor DIRECT_EXECUTOR = Runnable::run;

    private final InternalFutureCallableGroupCollapseExecutor<Object> collapseExecutor;

    public FutureCallableGroupCollapseExecutor() {
        this.collapseExecutor = new InternalFutureCallableGroupCollapseExecutor<>();
        this.collapseExecutor.setName(this.getClass().getSimpleName());
        setExecutor(DIRECT_EXECUTOR);
    }

    public FutureCallableGroupCollapseExecutor(ListeningCollector collector) {
        this.collapseExecutor = new InternalFutureCallableGroupCollapseExecutor<>(collector);
        this.collapseExecutor.setName(this.getClass().getSimpleName());
        setExecutor(DIRECT_EXECUTOR);
    }

    public void setExecutor(Executor executor) {
        this.collapseExecutor.setExecutor(executor);
    }

    public void setName(String name) {
        this.collapseExecutor.setName(name);
    }

    @SuppressWarnings("all")
    public <R> CompletableFuture<R> execute(Object group, ThrowableCallable<CompletableFuture<R>> callable) {
        CallableGroup<CompletableFuture<R>> callableGroup = new CallableGroup<>(group, callable);
        return (CompletableFuture<R>) collapseExecutor.execute((CallableGroup) callableGroup);
    }

    private static class InternalFutureCallableGroupCollapseExecutor<R> extends CollapseExecutorAsyncSupport<CallableGroup<CompletableFuture<R>>, R, CompletableFuture<R>> {

        public InternalFutureCallableGroupCollapseExecutor() {
            super();
        }

        private InternalFutureCallableGroupCollapseExecutor(ListeningCollector collector) {
            super(collector);
        }

        @Override
        protected CompletableFuture<R> doExecute(Collection<Input<CallableGroup<CompletableFuture<R>>>> inputs) throws Throwable {
            return inputs.iterator().next().value().getCallable().call();
        }

        @Override
        protected void bindingOutput(CompletableFuture<R> responseFuture, List<Bundle<CallableGroup<CompletableFuture<R>>, CompletableFuture<R>>> bundles) {
            for (Bundle<CallableGroup<CompletableFuture<R>>, CompletableFuture<R>> bundle : bundles) {
                bundle.bindOutput(responseFuture);
            }
        }
    }
}
