package cn.icodening.collapse.core.util;

/**
 * @author icodening
 * @date 2023.07.06
 */
@FunctionalInterface
public interface ThrowableCallable<V> {

    V call() throws Throwable;
}
