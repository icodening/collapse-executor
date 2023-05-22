package cn.icodening.tools.collapse.core.support;

import cn.icodening.tools.collapse.core.EqualsInputGrouper;
import cn.icodening.tools.collapse.core.Input;
import cn.icodening.tools.collapse.core.ListeningBundleCollector;
import cn.icodening.tools.collapse.core.SameOutputCollapseExecutorAsync;

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

    public AsyncCallableGroupCollapseExecutor(ListeningBundleCollector listeningBundleCollector) {
        this.asyncCallableGroupCollapseExecutor = new InternalCallableGroupCollapseExecutorAsync<>(listeningBundleCollector);
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

        private InternalCallableGroupCollapseExecutorAsync(ListeningBundleCollector collector) {
            super(collector);
            this.setInputGrouper(EqualsInputGrouper.getInstance());
        }

        @Override
        protected R doExecute(Collection<Input<CallableGroup<R>>> inputs) throws Throwable {
            return inputs.iterator().next().value().getCallable().call();
        }
    }
}
