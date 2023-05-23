package cn.icodening.tools.collapse.core;

import cn.icodening.tools.collapse.core.util.CacheableSupplier;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

/**
 * @author icodening
 * @date 2023.05.21
 */
public abstract class CollapseExecutorAsyncSupport<INPUT, OUTPUT, BATCH_OUTPUT> extends AbstractCollapseExecutor<INPUT, CompletableFuture<OUTPUT>, BATCH_OUTPUT> {

    private Supplier<Executor> executorSupplier;

    public CollapseExecutorAsyncSupport(ListeningBundleCollector collector) {
        super(collector);
    }

    public void setExecutor(Executor executor) {
        this.executorSupplier = () -> executor;
    }

    public void setExecutorSupplier(Supplier<Executor> executorSupplier) {
        this.executorSupplier = CacheableSupplier.from(executorSupplier);
    }

    @Override
    protected Executor getCallbackExecutor() {
        return executorSupplier.get();
    }

    @Override
    protected CompletableFuture<OUTPUT> returning(Bundle<INPUT, CompletableFuture<OUTPUT>> bundle) {
        CompletableFuture<OUTPUT> result = new CompletableFuture<>();
        CompletableFuture<CompletableFuture<OUTPUT>> listeningResult = bundle.getListeningResult();
        Executor executor = bundle.getCallbackExecutor();
        listeningResult.whenComplete((future, throwable) -> {
            if (throwable != null) {
                result.completeExceptionally(throwable);
            } else {
                future.whenComplete((output, ex) -> {
                    executor.execute(() -> {
                        if (ex != null) {
                            result.completeExceptionally(ex);
                        } else {
                            result.complete(output);
                        }
                    });
                });
            }
        });
        return result;
    }
}
