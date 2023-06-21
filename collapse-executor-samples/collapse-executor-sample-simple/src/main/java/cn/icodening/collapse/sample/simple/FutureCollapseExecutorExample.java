package cn.icodening.collapse.sample.simple;

import cn.icodening.collapse.core.SingleThreadExecutor;
import cn.icodening.collapse.core.SuspendableListenableCollector;
import cn.icodening.collapse.core.support.FutureCallableGroupCollapseExecutor;

import java.util.concurrent.CompletableFuture;

/**
 * @author icodening
 * @date 2023.06.20
 */
public class FutureCollapseExecutorExample {

    public static void main(String[] args) throws Throwable {
        SingleThreadExecutor singleThreadExecutor = new SingleThreadExecutor();
        SuspendableListenableCollector suspendableListeningBundleCollector = new SuspendableListenableCollector(singleThreadExecutor);
        FutureCallableGroupCollapseExecutor futureCollapseExecutor = new FutureCallableGroupCollapseExecutor(suspendableListeningBundleCollector);
        futureCollapseExecutor.execute("example group", () -> {
            CompletableFuture<String> result = new CompletableFuture<>();
            result.complete("Hello World Collapse Executor. Future");
            return result;
        }).thenAccept(System.out::println).thenRun(() -> System.exit(0));
        System.in.read();
    }
}
