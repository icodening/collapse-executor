package cn.icodening.collapse.core.support;

import cn.icodening.collapse.core.Input;
import cn.icodening.collapse.core.ListenableCollector;
import cn.icodening.collapse.core.BlockingSameOutputCollapseExecutor;
import cn.icodening.collapse.core.EqualsInputGrouper;

import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.Callable;

/**
 * @author icodening
 * @date 2023.05.14
 */
public final class BlockingCallableGroupCollapseExecutor {

    private final InternalBlockingCallableGroupCollapseExecutorBlocking<Object> executor;

    public BlockingCallableGroupCollapseExecutor(ListenableCollector collector) {
        Objects.requireNonNull(collector, "collector must be not null.");
        this.executor = new InternalBlockingCallableGroupCollapseExecutorBlocking<>(collector);
    }

    @SuppressWarnings("unchecked")
    public <R> R execute(Object group, Callable<R> callable) throws Throwable {
        CallableGroup<R> callableGroup = new CallableGroup<>(group, callable);
        return (R) executor.execute((CallableGroup<Object>) callableGroup);
    }

    private static class InternalBlockingCallableGroupCollapseExecutorBlocking<R> extends BlockingSameOutputCollapseExecutor<CallableGroup<R>, R> {

        private InternalBlockingCallableGroupCollapseExecutorBlocking(ListenableCollector collector) {
            super(collector);
            this.setInputGrouper(EqualsInputGrouper.getInstance());
        }

        @Override
        protected R doExecute(Collection<Input<CallableGroup<R>>> inputs) throws Throwable {
            return inputs.iterator().next().value().getCallable().call();
        }
    }
}
