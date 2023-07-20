package cn.icodening.collapse.core.support;

import cn.icodening.collapse.core.Bundle;
import cn.icodening.collapse.core.CollapseExecutorAsyncSupport;
import cn.icodening.collapse.core.Input;
import cn.icodening.collapse.core.ListeningCollector;
import cn.icodening.collapse.core.util.ThrowableCallable;

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
