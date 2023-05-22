package cn.icodening.tools.collapse.core.support;

import cn.icodening.tools.collapse.core.EqualsInputGrouper;
import cn.icodening.tools.collapse.core.Input;
import cn.icodening.tools.collapse.core.ListeningBundleCollector;
import cn.icodening.tools.collapse.core.SameOutputCollapseExecutor;

import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.Callable;

/**
 * @author icodening
 * @date 2023.05.14
 */
public final class CallableGroupCollapseExecutor {

    private final InternalCallableGroupCollapseExecutor<Object> executor;

    public CallableGroupCollapseExecutor(ListeningBundleCollector collector) {
        Objects.requireNonNull(collector, "collector must be not null.");
        this.executor = new InternalCallableGroupCollapseExecutor<>(collector);
    }

    @SuppressWarnings("unchecked")
    public <R> R execute(Object group, Callable<R> callable) throws Throwable {
        CallableGroup<R> callableGroup = new CallableGroup<>(group, callable);
        return (R) executor.execute((CallableGroup<Object>) callableGroup);
    }

    private static class InternalCallableGroupCollapseExecutor<R> extends SameOutputCollapseExecutor<CallableGroup<R>, R> {

        private InternalCallableGroupCollapseExecutor(ListeningBundleCollector collector) {
            super(collector);
            this.setInputGrouper(EqualsInputGrouper.getInstance());
        }

        @Override
        protected R doExecute(Collection<Input<CallableGroup<R>>> inputs) throws Throwable {
            return inputs.iterator().next().value().getCallable().call();
        }
    }
}
