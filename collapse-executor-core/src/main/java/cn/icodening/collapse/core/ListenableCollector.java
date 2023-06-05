package cn.icodening.collapse.core;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;

/**
 * @author icodening
 * @date 2023.05.14
 */
public class ListenableCollector extends GroupListeningBatchCollector<CollapseExecutor<?, ?>, Bundle<Object, Object>> {

    public ListenableCollector(Executor dispatcher) {
        super(dispatcher, Bundle::getCollapseExecutor, new ConcurrentHashMap<>(256));
    }

}
