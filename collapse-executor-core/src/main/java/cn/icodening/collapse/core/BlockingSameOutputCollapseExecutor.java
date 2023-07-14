package cn.icodening.collapse.core;

import java.util.List;

/**
 * @author icodening
 * @date 2023.05.14
 */
public abstract class BlockingSameOutputCollapseExecutor<INPUT, OUTPUT> extends CollapseExecutorBlockingSupport<INPUT, OUTPUT, OUTPUT> {

    public BlockingSameOutputCollapseExecutor() {
        super();
    }

    public BlockingSameOutputCollapseExecutor(ListeningCollector collector) {
        super(collector);
    }

    @Override
    protected void bindingOutput(OUTPUT batchOutput, List<Bundle<INPUT, OUTPUT>> bundles) {
        for (Bundle<INPUT, OUTPUT> bundle : bundles) {
            bundle.bindOutput(batchOutput);
        }
    }
}
