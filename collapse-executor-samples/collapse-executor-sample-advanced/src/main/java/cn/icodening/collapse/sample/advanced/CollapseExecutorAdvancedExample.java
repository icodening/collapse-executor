/*
 * Copyright 2023-2024 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.icodening.collapse.sample.advanced;

import cn.icodening.collapse.core.SuspendableCollector;
import cn.icodening.collapse.sample.advanced.support.CustomBlockingCollapseExecutor;
import cn.icodening.collapse.sample.advanced.support.UserEntity;
import cn.icodening.collapse.sample.advanced.support.UserService;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author icodening
 * @date 2023.06.27
 */
public class CollapseExecutorAdvancedExample {

    private static final ExecutorService BIZ_EXECUTOR_SERVICE = Executors.newFixedThreadPool(50, r -> {
        Thread thread = new Thread(r);
        thread.setDaemon(true);
        return thread;
    });

    public static void main(String[] args) throws InterruptedException {
        SuspendableCollector listenableCollector = new SuspendableCollector();
        CustomBlockingCollapseExecutor customBlockingCollapseExecutor = new CustomBlockingCollapseExecutor(listenableCollector, new UserService());
        customBlockingCollapseExecutor.setBatchSize(5);
        //query id [1,13]
        int max = 13;
        CountDownLatch blockingWaiter = new CountDownLatch(max);
        for (long i = 1; i <= max; i++) {
            Long queryId = i;
            BIZ_EXECUTOR_SERVICE.execute(() -> {
                try {
                    UserEntity userEntity = customBlockingCollapseExecutor.execute(queryId);
                    System.out.println(Thread.currentThread().getName() + " query id [" + queryId + "], result is:" + userEntity);
                } catch (Throwable e) {
                    e.printStackTrace();
                } finally {
                    blockingWaiter.countDown();
                }
            });
        }
        blockingWaiter.await();
    }

}
