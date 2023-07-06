package cn.icodening.collapse.core.support;

import cn.icodening.collapse.core.BlockingSameOutputCollapseExecutor;
import cn.icodening.collapse.core.Input;
import cn.icodening.collapse.core.ListeningCollector;
import cn.icodening.collapse.core.util.ThrowableCallable;

import java.util.Collection;
import java.util.Objects;

/**
 * @author icodening
 * @date 2023.05.14
 */
public final class BlockingCallableGroupCollapseExecutor {

    private final InternalBlockingCallableGroupCollapseExecutor<Object> collapseExecutor;

    public BlockingCallableGroupCollapseExecutor(ListeningCollector collector) {
        Objects.requireNonNull(collector, "collector must be not null.");
        this.collapseExecutor = new InternalBlockingCallableGroupCollapseExecutor<>(collector);
    }

    public void setName(String name) {
        this.collapseExecutor.setName(name);
    }

    @SuppressWarnings("unchecked")
    public <R> R execute(Object group, ThrowableCallable<R> callable) throws Throwable {
        CallableGroup<R> callableGroup = new CallableGroup<>(group, callable);
        return (R) collapseExecutor.execute((CallableGroup<Object>) callableGroup);
    }

    private static class InternalBlockingCallableGroupCollapseExecutor<R> extends BlockingSameOutputCollapseExecutor<CallableGroup<R>, R> {

        private InternalBlockingCallableGroupCollapseExecutor(ListeningCollector collector) {
            super(collector);
            this.setName(BlockingCallableGroupCollapseExecutor.class.getSimpleName());
        }

        @Override
        protected R doExecute(Collection<Input<CallableGroup<R>>> inputs) throws Throwable {
            return inputs.iterator().next().value().getCallable().call();
        }
    }
}
