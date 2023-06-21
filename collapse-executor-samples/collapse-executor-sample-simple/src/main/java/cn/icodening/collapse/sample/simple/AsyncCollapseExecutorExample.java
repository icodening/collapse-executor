package cn.icodening.collapse.sample.simple;

import cn.icodening.collapse.core.SingleThreadExecutor;
import cn.icodening.collapse.core.SuspendableListenableCollector;
import cn.icodening.collapse.core.support.AsyncCallableGroupCollapseExecutor;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author icodening
 * @date 2023.06.20
 */
public class AsyncCollapseExecutorExample {

    public static void main(String[] args) throws Throwable {
        SingleThreadExecutor singleThreadExecutor = new SingleThreadExecutor();
        SuspendableListenableCollector suspendableListeningBundleCollector = new SuspendableListenableCollector(singleThreadExecutor);
        AsyncCallableGroupCollapseExecutor asyncCallableGroupCollapseExecutor = new AsyncCallableGroupCollapseExecutor(suspendableListeningBundleCollector);
        asyncCallableGroupCollapseExecutor.setExecutor(new ThreadPoolExecutor(50, 50, 0, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(), r -> {
            Thread thread = new Thread(r);
            thread.setDaemon(true);
            return thread;
        }));
        asyncCallableGroupCollapseExecutor.execute("example group",
                        () -> "Hello World Collapse Executor. Async")
                .thenAccept(System.out::println).thenRun(() -> System.exit(0));
        System.in.read();
    }
}
