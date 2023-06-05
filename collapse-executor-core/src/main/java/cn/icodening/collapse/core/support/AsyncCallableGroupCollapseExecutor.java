package cn.icodening.collapse.core.support;

import cn.icodening.collapse.core.EqualsInputGrouper;
import cn.icodening.collapse.core.Input;
import cn.icodening.collapse.core.ListenableCollector;
import cn.icodening.collapse.core.SameOutputCollapseExecutorAsync;

import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * @author icodening
 * @date 2023.05.21
 */
public class AsyncCallableGroupCollapseExecutor {

    private final InternalCallableGroupCollapseExecutorAsync<Object> asyncCallableGroupCollapseExecutor;

    public AsyncCallableGroupCollapseExecutor(ListenableCollector listenableCollector) {
        this.asyncCallableGroupCollapseExecutor = new InternalCallableGroupCollapseExecutorAsync<>(listenableCollector);
    }

    public void setExecutor(Executor executor) {
        this.asyncCallableGroupCollapseExecutor.setExecutor(executor);
    }

    @SuppressWarnings("unchecked")
    public <R> CompletableFuture<R> execute(Object group, Callable<R> callable) throws Throwable {
        CallableGroup<R> callableGroup = new CallableGroup<>(group, callable);
        return (CompletableFuture<R>) asyncCallableGroupCollapseExecutor.execute((CallableGroup<Object>) callableGroup);
    }

    private static class InternalCallableGroupCollapseExecutorAsync<R> extends SameOutputCollapseExecutorAsync<CallableGroup<R>, R> {

        private InternalCallableGroupCollapseExecutorAsync(ListenableCollector collector) {
            super(collector);
            this.setInputGrouper(EqualsInputGrouper.getInstance());
        }

        @Override
        protected R doExecute(Collection<Input<CallableGroup<R>>> inputs) throws Throwable {
            return inputs.iterator().next().value().getCallable().call();
        }
    }
}
