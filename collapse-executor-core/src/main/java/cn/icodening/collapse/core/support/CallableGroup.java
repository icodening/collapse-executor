package cn.icodening.collapse.core.support;

import cn.icodening.collapse.core.util.ThrowableCallable;

import java.util.Objects;

/**
 * @author icodening
 * @date 2023.05.22
 */
class CallableGroup<R> {

    private final Object group;

    private final ThrowableCallable<R> callable;

    CallableGroup(Object group, ThrowableCallable<R> callable) {
        this.group = group;
        this.callable = callable;
    }

    public Object getGroup() {
        return group;
    }

    public ThrowableCallable<R> getCallable() {
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
