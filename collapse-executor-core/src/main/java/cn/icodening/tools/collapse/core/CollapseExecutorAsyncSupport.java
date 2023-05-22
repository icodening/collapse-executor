package cn.icodening.tools.collapse.core;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * @author icodening
 * @date 2023.05.21
 */
public abstract class CollapseExecutorAsyncSupport<INPUT, OUTPUT, BATCH_OUTPUT> extends AbstractCollapseExecutor<INPUT, CompletableFuture<OUTPUT>, BATCH_OUTPUT> {

    private Executor executor;

    public CollapseExecutorAsyncSupport(ListeningBundleCollector collector) {
        super(collector);
    }

    public void setExecutor(Executor executor) {
        this.executor = executor;
    }

    @Override
    @SuppressWarnings("unchecked")
    public CompletableFuture<OUTPUT> execute(INPUT input) {
        CompletableFuture<OUTPUT> result = new CompletableFuture<>();
        this.getCollector().enqueue((Bundle<Object, Object>) createBundle(input, executor, adaptFuture(new CompletableFuture<>(), result)));
        return result;
    }

    private CompletableFuture<CompletableFuture<OUTPUT>> adaptFuture(CompletableFuture<CompletableFuture<OUTPUT>> bundleFuture, CompletableFuture<OUTPUT> actualFuture) {
        return bundleFuture.whenComplete((future, throwable) -> {
            if (throwable != null) {
                actualFuture.completeExceptionally(throwable);
            } else {
                future.whenComplete((output, ex) -> {
                    if (ex != null) {
                        actualFuture.completeExceptionally(ex);
                    } else {
                        actualFuture.complete(output);
                    }
                });
            }
        });
    }

    private Bundle<?, ?> createBundle(INPUT input, Executor executor, CompletableFuture<CompletableFuture<OUTPUT>> completableFuture) {
        return new Bundle<>(this, input, executor, completableFuture);
    }
}
