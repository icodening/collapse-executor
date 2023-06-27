package cn.icodening.collapse.core.support;

import cn.icodening.collapse.core.Bundle;
import cn.icodening.collapse.core.CollapseExecutorAsyncSupport;
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

    private static final Executor DIRECT_EXECUTOR = Runnable::run;

    private final InternalFutureCallableGroupCollapseExecutor<Object> asyncCallableGroupCollapseExecutor;

    public FutureCallableGroupCollapseExecutor(ListenableCollector listenableCollector) {
        this.asyncCallableGroupCollapseExecutor = new InternalFutureCallableGroupCollapseExecutor<>(listenableCollector);
        setExecutor(DIRECT_EXECUTOR);
    }

    public void setExecutor(Executor executor) {
        this.asyncCallableGroupCollapseExecutor.setExecutor(executor);
    }

    @SuppressWarnings("all")
    public <R> CompletableFuture<R> execute(Object group, Callable<CompletableFuture<R>> callable) {
        CallableGroup<CompletableFuture<R>> callableGroup = new CallableGroup<>(group, callable);
        return (CompletableFuture<R>) asyncCallableGroupCollapseExecutor.execute((CallableGroup) callableGroup);
    }

    private static class InternalFutureCallableGroupCollapseExecutor<R> extends CollapseExecutorAsyncSupport<CallableGroup<CompletableFuture<R>>, R, CompletableFuture<R>> {

        private InternalFutureCallableGroupCollapseExecutor(ListenableCollector collector) {
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
