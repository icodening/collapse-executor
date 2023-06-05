package cn.icodening.collapse.core.support;

import cn.icodening.collapse.core.Input;
import cn.icodening.collapse.core.ListeningBundleCollector;
import cn.icodening.collapse.core.SameOutputCollapseExecutorSync;
import cn.icodening.collapse.core.EqualsInputGrouper;

import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.Callable;

/**
 * @author icodening
 * @date 2023.05.14
 */
public final class SyncCallableGroupCollapseExecutor {

    private final InternalCallableGroupCollapseExecutorSync<Object> executor;

    public SyncCallableGroupCollapseExecutor(ListeningBundleCollector collector) {
        Objects.requireNonNull(collector, "collector must be not null.");
        this.executor = new InternalCallableGroupCollapseExecutorSync<>(collector);
    }

    @SuppressWarnings("unchecked")
    public <R> R execute(Object group, Callable<R> callable) throws Throwable {
        CallableGroup<R> callableGroup = new CallableGroup<>(group, callable);
        return (R) executor.execute((CallableGroup<Object>) callableGroup);
    }

    private static class InternalCallableGroupCollapseExecutorSync<R> extends SameOutputCollapseExecutorSync<CallableGroup<R>, R> {

        private InternalCallableGroupCollapseExecutorSync(ListeningBundleCollector collector) {
            super(collector);
            this.setInputGrouper(EqualsInputGrouper.getInstance());
        }

        @Override
        protected R doExecute(Collection<Input<CallableGroup<R>>> inputs) throws Throwable {
            return inputs.iterator().next().value().getCallable().call();
        }
    }
}
