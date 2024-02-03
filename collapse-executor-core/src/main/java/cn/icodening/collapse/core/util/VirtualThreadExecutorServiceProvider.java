package cn.icodening.collapse.core.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author icodening
 * @date 2024.02.03
 */
public abstract class VirtualThreadExecutorServiceProvider {

    private VirtualThreadExecutorServiceProvider() {
    }

    public static ExecutorService tryInstantiateVitrualThreadExecutorService() {
        return initVirtualThreadExecutorService();
    }

    private static ExecutorService initVirtualThreadExecutorService() {
        try {
            return (ExecutorService) Executors.class
                    .getDeclaredMethod("newVirtualThreadPerTaskExecutor")
                    .invoke(null);
        } catch (Throwable ignored) {
            return null;
        }
    }
}
