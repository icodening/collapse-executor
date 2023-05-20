package cn.icodening.tools.collapse.core;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * @author icodening
 * @date 2023.05.19
 */
public class AsyncCallableGroupCollapseExecutor {

    private final CallableGroupCollapseExecutor callableGroupCollapseExecutor;

    /**
     * When high concurrency and callable is non-blocking, direct executor is better.
     * <p>
     * For example:
     * <pre> {@code
     * private Executor executor = Runnable::run;
     * }</pre>
     * </p>
     */
    private Executor executor;

    public AsyncCallableGroupCollapseExecutor(CallableGroupCollapseExecutor callableGroupCollapseExecutor) {
        this.callableGroupCollapseExecutor = callableGroupCollapseExecutor;
    }

    public void setExecutor(Executor executor) {
        this.executor = executor;
    }

    public <R> CompletableFuture<R> execute(Object group, Callable<R> callable) throws Throwable {
        CompletableFuture<R> completableFuture = new CompletableFuture<>();
        executor.execute(() -> {
            try {
                completableFuture.complete(callableGroupCollapseExecutor.execute(group, callable));
            } catch (Throwable e) {
                completableFuture.completeExceptionally(e);
            }
        });
        return completableFuture;
    }

}
