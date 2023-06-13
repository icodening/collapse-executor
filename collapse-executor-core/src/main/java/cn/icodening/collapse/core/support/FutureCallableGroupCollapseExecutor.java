package cn.icodening.collapse.core.support;

import cn.icodening.collapse.core.Bundle;
import cn.icodening.collapse.core.CollapseExecutorAsyncSupport;
import cn.icodening.collapse.core.EqualsInputGrouper;
import cn.icodening.collapse.core.Input;
import cn.icodening.collapse.core.ListenableCollector;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * @author icodening
 * @date 2023.06.09
 */
public class FutureCallableGroupCollapseExecutor {

    private final InternalCallableGroupCollapseExecutorAsync<Object> asyncCallableGroupCollapseExecutor;

    public FutureCallableGroupCollapseExecutor(ListenableCollector listenableCollector) {
        this.asyncCallableGroupCollapseExecutor = new InternalCallableGroupCollapseExecutorAsync<>(listenableCollector);
    }

    public void setExecutor(Executor executor) {
        this.asyncCallableGroupCollapseExecutor.setExecutor(executor);
    }

    @SuppressWarnings("all")
    public <R> CompletableFuture<R> execute(Object group, Callable<CompletableFuture<R>> callable) {
        CallableGroup<CompletableFuture<R>> callableGroup = new CallableGroup<>(group, callable);
        return (CompletableFuture<R>) asyncCallableGroupCollapseExecutor.execute((CallableGroup) callableGroup);
    }

    private static class InternalCallableGroupCollapseExecutorAsync<R> extends CollapseExecutorAsyncSupport<CallableGroup<CompletableFuture<R>>, R, CompletableFuture<R>> {

        private InternalCallableGroupCollapseExecutorAsync(ListenableCollector collector) {
            super(collector);
            this.setInputGrouper(EqualsInputGrouper.getInstance());
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
