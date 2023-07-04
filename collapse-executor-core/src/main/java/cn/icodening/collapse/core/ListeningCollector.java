package cn.icodening.collapse.core;

import java.util.concurrent.Executor;

/**
 * @author icodening
 * @date 2023.05.14
 */
public class ListeningCollector extends GroupListeningBatchCollector<CollapseExecutor<?, ?>, Bundle<Object, Object>> {

    public ListeningCollector() {
        super(Bundle::getCollapseExecutor);
    }

    public ListeningCollector(Executor dispatcher) {
        super(dispatcher, Bundle::getCollapseExecutor);
    }

}
