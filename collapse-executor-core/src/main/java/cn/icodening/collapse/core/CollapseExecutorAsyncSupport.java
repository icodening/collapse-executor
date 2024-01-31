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
package cn.icodening.collapse.core;

import cn.icodening.collapse.core.util.CacheableSupplier;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

/**
 * @author icodening
 * @date 2023.05.21
 */
public abstract class CollapseExecutorAsyncSupport<INPUT, OUTPUT, BATCH_OUTPUT> extends AbstractCollapseExecutor<INPUT, CompletableFuture<OUTPUT>, BATCH_OUTPUT> {

    private Supplier<Executor> executorSupplier;

    public CollapseExecutorAsyncSupport() {
        super();
    }

    public CollapseExecutorAsyncSupport(ListeningCollector collector) {
        super(collector);
    }

    public void setExecutor(Executor executor) {
        this.executorSupplier = () -> executor;
    }

    public void setExecutorSupplier(Supplier<Executor> executorSupplier) {
        this.executorSupplier = CacheableSupplier.from(executorSupplier);
    }

    @Override
    protected Executor getCallbackExecutor() {
        return executorSupplier.get();
    }

    @Override
    public CompletableFuture<OUTPUT> execute(INPUT input) {
        try {
            return super.execute(input);
        } catch (Throwable e) {
            CompletableFuture<OUTPUT> result = new CompletableFuture<>();
            result.completeExceptionally(e);
            return result;
        }
    }

    @Override
    protected CompletableFuture<OUTPUT> returning(Bundle<INPUT, CompletableFuture<OUTPUT>> bundle) {
        CompletableFuture<OUTPUT> result = new CompletableFuture<>();
        CompletableFuture<CompletableFuture<OUTPUT>> listeningResult = bundle.getListeningResult();
        listeningResult.whenComplete((future, throwable) -> {
            if (throwable != null) {
                result.completeExceptionally(throwable);
            } else {
                future.whenComplete((output, ex) -> {
                    if (ex != null) {
                        result.completeExceptionally(ex);
                    } else {
                        result.complete(output);
                    }
                });
            }
        });
        return result;
    }
}
