package cn.icodening.collapse.core.support;

import cn.icodening.collapse.core.AsyncSameOutputCollapseExecutor;
import cn.icodening.collapse.core.Input;
import cn.icodening.collapse.core.ListenableCollector;

import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * @author icodening
 * @date 2023.05.21
 */
public class AsyncCallableGroupCollapseExecutor {

    private final InternalSameOutputCollapseExecutorAsyncCallableGroupCollapseExecutor<Object> asyncCallableGroupCollapseExecutor;

    public AsyncCallableGroupCollapseExecutor(ListenableCollector listenableCollector) {
        this.asyncCallableGroupCollapseExecutor = new InternalSameOutputCollapseExecutorAsyncCallableGroupCollapseExecutor<>(listenableCollector);
    }

    public void setExecutor(Executor executor) {
        this.asyncCallableGroupCollapseExecutor.setExecutor(executor);
    }

    @SuppressWarnings("unchecked")
    public <R> CompletableFuture<R> execute(Object group, Callable<R> callable) {
        CallableGroup<R> callableGroup = new CallableGroup<>(group, callable);
        return (CompletableFuture<R>) asyncCallableGroupCollapseExecutor.execute((CallableGroup<Object>) callableGroup);
    }

    private static class InternalSameOutputCollapseExecutorAsyncCallableGroupCollapseExecutor<R> extends AsyncSameOutputCollapseExecutor<CallableGroup<R>, R> {

        private InternalSameOutputCollapseExecutorAsyncCallableGroupCollapseExecutor(ListenableCollector collector) {
            super(collector);
        }

        @Override
        protected R doExecute(Collection<Input<CallableGroup<R>>> inputs) throws Throwable {
            return inputs.iterator().next().value().getCallable().call();
        }
    }
}
