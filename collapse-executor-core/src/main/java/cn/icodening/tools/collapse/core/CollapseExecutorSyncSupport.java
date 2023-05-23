package cn.icodening.tools.collapse.core;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

/**
 * @author icodening
 * @date 2023.05.23
 */
public abstract class CollapseExecutorSyncSupport<INPUT, OUTPUT, BATCH_OUTPUT> extends AbstractCollapseExecutor<INPUT, OUTPUT, BATCH_OUTPUT> {

    public CollapseExecutorSyncSupport(ListeningBundleCollector collector) {
        super(collector);
    }

    @Override
    protected final OUTPUT returning(Bundle<INPUT, OUTPUT> bundle) throws Throwable {
        ThreadlessExecutor threadlessExecutor = (ThreadlessExecutor) bundle.getCallbackExecutor();
        CompletableFuture<OUTPUT> completableFuture = bundle.getListeningResult();
        try {
            while (!completableFuture.isDone()) {
                threadlessExecutor.waitAndDrain();
            }
            return completableFuture.get();
        } catch (ExecutionException executionException) {
            //throw actual exception
            throw executionException.getCause();
        } finally {
            threadlessExecutor.shutdown();
        }
    }

    @Override
    protected final Executor getCallbackExecutor() {
        return new ThreadlessExecutor();
    }
}
