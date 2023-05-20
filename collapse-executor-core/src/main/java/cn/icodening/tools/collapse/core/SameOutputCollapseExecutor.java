package cn.icodening.tools.collapse.core;

import java.util.List;

/**
 * @author icodening
 * @date 2023.05.14
 */
public abstract class SameOutputCollapseExecutor<INPUT, OUTPUT> extends AbstractCollapseExecutor<INPUT, OUTPUT, OUTPUT> {

    public SameOutputCollapseExecutor(ListeningBundleCollector collector) {
        super(collector);
    }

    @Override
    protected void bindingOutput(OUTPUT batchOutput, List<Bundle<INPUT, OUTPUT>> bundles) {
        for (Bundle<INPUT, OUTPUT> bundle : bundles) {
            bundle.bindOutput(batchOutput);
        }
    }
}
