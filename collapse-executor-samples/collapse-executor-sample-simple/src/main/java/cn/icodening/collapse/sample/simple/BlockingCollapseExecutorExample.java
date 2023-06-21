package cn.icodening.collapse.sample.simple;

import cn.icodening.collapse.core.SingleThreadExecutor;
import cn.icodening.collapse.core.SuspendableListenableCollector;
import cn.icodening.collapse.core.support.BlockingCallableGroupCollapseExecutor;

/**
 * @author icodening
 * @date 2023.06.20
 */
public class BlockingCollapseExecutorExample {

    public static void main(String[] args) throws Throwable {
        SingleThreadExecutor singleThreadExecutor = new SingleThreadExecutor();
        SuspendableListenableCollector suspendableListeningBundleCollector = new SuspendableListenableCollector(singleThreadExecutor);
        BlockingCallableGroupCollapseExecutor blockingCollapseExecutor = new BlockingCallableGroupCollapseExecutor(suspendableListeningBundleCollector);
        String responseString = blockingCollapseExecutor.execute("example group", () -> "Hello World Collapse Executor. Blocking");
        System.out.println(responseString);
    }
}
