package cn.icodening.collapse.core;

import java.util.List;

/**
 * @author icodening
 * @date 2023.05.14
 */
public abstract class SameOutputCollapseExecutorSync<INPUT, OUTPUT> extends CollapseExecutorSyncSupport<INPUT, OUTPUT, OUTPUT> {

    public SameOutputCollapseExecutorSync(ListenableCollector collector) {
        super(collector);
    }

    @Override
    protected void bindingOutput(OUTPUT batchOutput, List<Bundle<INPUT, OUTPUT>> bundles) {
        for (Bundle<INPUT, OUTPUT> bundle : bundles) {
            bundle.bindOutput(batchOutput);
        }
    }
}
