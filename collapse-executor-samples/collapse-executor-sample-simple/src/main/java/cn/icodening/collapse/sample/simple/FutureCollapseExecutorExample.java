package cn.icodening.collapse.sample.simple;

import cn.icodening.collapse.core.SuspendableCollector;
import cn.icodening.collapse.core.support.FutureCallableGroupCollapseExecutor;

import java.util.concurrent.CompletableFuture;

/**
 * @author icodening
 * @date 2023.06.20
 */
public class FutureCollapseExecutorExample {

    public static void main(String[] args) throws Throwable {
        SuspendableCollector suspendableCollector = new SuspendableCollector();
        FutureCallableGroupCollapseExecutor futureCollapseExecutor = new FutureCallableGroupCollapseExecutor(suspendableCollector);
        futureCollapseExecutor.execute("example group", () -> CompletableFuture.completedFuture("Hello World Collapse Executor. Future"))
                .thenAccept(System.out::println)
                .thenRun(() -> System.exit(0));
        System.in.read();
    }
}
