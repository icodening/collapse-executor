package cn.icodening.collapse.core.support;

import cn.icodening.collapse.core.BlockingSameOutputCollapseExecutor;
import cn.icodening.collapse.core.Input;
import cn.icodening.collapse.core.ListeningCollector;
import cn.icodening.collapse.core.util.ThrowableCallable;

import java.util.Collection;

/**
 * @author icodening
 * @date 2023.05.14
 */
public class BlockingCallableGroupCollapseExecutor {

    private final InternalBlockingCallableGroupCollapseExecutor<Object> collapseExecutor;

    public BlockingCallableGroupCollapseExecutor() {
        this.collapseExecutor = new InternalBlockingCallableGroupCollapseExecutor<>();
        this.collapseExecutor.setName(this.getClass().getSimpleName());
    }

    public BlockingCallableGroupCollapseExecutor(ListeningCollector collector) {
        this.collapseExecutor = new InternalBlockingCallableGroupCollapseExecutor<>(collector);
        this.collapseExecutor.setName(this.getClass().getSimpleName());
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

        public InternalBlockingCallableGroupCollapseExecutor() {
            super();
        }

        private InternalBlockingCallableGroupCollapseExecutor(ListeningCollector collector) {
            super(collector);
        }

        @Override
        protected R doExecute(Collection<Input<CallableGroup<R>>> inputs) throws Throwable {
            return inputs.iterator().next().value().getCallable().call();
        }
    }
}
