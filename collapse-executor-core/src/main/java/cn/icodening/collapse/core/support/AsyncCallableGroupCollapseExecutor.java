package cn.icodening.collapse.core.support;

import cn.icodening.collapse.core.AsyncSameOutputCollapseExecutor;
import cn.icodening.collapse.core.Input;
import cn.icodening.collapse.core.ListeningCollector;
import cn.icodening.collapse.core.util.ThrowableCallable;

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
        this.collapseExecutor.setName(AsyncCallableGroupCollapseExecutor.class.getSimpleName());
    }

    public AsyncCallableGroupCollapseExecutor(ListeningCollector collector) {
        this.collapseExecutor = new InternalSameOutputCollapseExecutorAsyncCallableGroupCollapseExecutor<>(collector);
        this.collapseExecutor.setName(AsyncCallableGroupCollapseExecutor.class.getSimpleName());
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
