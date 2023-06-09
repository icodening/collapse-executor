package cn.icodening.collapse.core;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @author icodening
 * @date 2023.05.21
 */
public abstract class AsyncSameOutputCollapseExecutor<INPUT, OUTPUT> extends CollapseExecutorAsyncSupport<INPUT, OUTPUT, OUTPUT> {

    public AsyncSameOutputCollapseExecutor(ListeningCollector collector) {
        super(collector);
    }

    @Override
    protected void bindingOutput(OUTPUT batchOutput, List<Bundle<INPUT, CompletableFuture<OUTPUT>>> bundles) {
        for (Bundle<INPUT, CompletableFuture<OUTPUT>> bundle : bundles) {
            bundle.bindOutput(CompletableFuture.completedFuture(batchOutput));
        }
    }
}
