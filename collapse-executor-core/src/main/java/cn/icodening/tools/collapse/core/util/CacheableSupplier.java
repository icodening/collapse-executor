package cn.icodening.tools.collapse.core.util;

import java.util.function.Supplier;

/**
 * @author icodening
 * @date 2023.05.22
 */
public class CacheableSupplier<T> implements Supplier<T> {

    private final Supplier<T> supplier;

    private volatile T object;

    private volatile boolean initialized = false;

    private CacheableSupplier(Supplier<T> supplier) {
        this.supplier = supplier;
    }

    public static <T> Supplier<T> from(Supplier<T> supplier) {
        return new CacheableSupplier<>(supplier);
    }

    @Override
    public T get() {
        if (initialized) {
            return object;
        }
        synchronized (this) {
            if (!initialized) {
                this.object = supplier.get();
                this.initialized = true;
            }
        }
        return object;
    }
}
