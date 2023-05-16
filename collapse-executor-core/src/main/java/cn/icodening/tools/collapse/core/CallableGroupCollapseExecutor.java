package cn.icodening.tools.collapse.core;

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

    private static class CallableGroup<R> {

        private final Object group;

        private final Callable<R> callable;

        public CallableGroup(Object group, Callable<R> callable) {
            this.group = group;
            this.callable = callable;
        }

        public Object getGroup() {
            return group;
        }

        public Callable<R> getCallable() {
            return callable;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            CallableGroup<?> that = (CallableGroup<?>) o;
            return Objects.equals(group, that.group);
        }

        @Override
        public int hashCode() {
            return Objects.hash(group);
        }
    }
}
