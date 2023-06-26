package cn.icodening.collapse.sample.advanced;

import cn.icodening.collapse.core.SingleThreadExecutor;
import cn.icodening.collapse.core.SuspendableListenableCollector;
import cn.icodening.collapse.sample.advanced.support.CustomBlockingCollapseExecutor;
import cn.icodening.collapse.sample.advanced.support.UserEntity;
import cn.icodening.collapse.sample.advanced.support.UserService;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author icodening
 * @date 2023.06.27
 */
public class CollapseExecutorAdvancedExample {

    private static final ExecutorService EXECUTOR_SERVICE = Executors.newFixedThreadPool(20, r -> {
        Thread thread = new Thread(r);
        thread.setDaemon(true);
        return thread;
    });

    public static void main(String[] args) throws InterruptedException {
        SuspendableListenableCollector listenableCollector = new SuspendableListenableCollector(new SingleThreadExecutor());
        CustomBlockingCollapseExecutor customBlockingCollapseExecutor = new CustomBlockingCollapseExecutor(listenableCollector, new UserService());
        customBlockingCollapseExecutor.setBatchSize(5);
        //query id [1,12]
        for (long i = 1; i <= 12; i++) {
            Long queryId = i;
            EXECUTOR_SERVICE.execute(() -> {
                try {
                    UserEntity userEntity = customBlockingCollapseExecutor.execute(queryId);
                    System.out.println(Thread.currentThread().getName() + " query id [" + queryId + "], result is:" + userEntity);
                } catch (Throwable e) {
                    throw new RuntimeException(e);
                }
            });
        }
        Thread.sleep(1000);
    }

}
