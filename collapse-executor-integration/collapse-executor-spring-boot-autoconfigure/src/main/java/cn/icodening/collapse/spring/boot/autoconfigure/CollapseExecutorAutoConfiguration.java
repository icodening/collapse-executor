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
package cn.icodening.collapse.spring.boot.autoconfigure;

import cn.icodening.collapse.core.ListeningCollector;
import cn.icodening.collapse.core.SuspendableCollector;
import cn.icodening.collapse.core.support.AsyncCallableGroupCollapseExecutor;
import cn.icodening.collapse.core.support.BlockingCallableGroupCollapseExecutor;
import cn.icodening.collapse.core.support.FutureCallableGroupCollapseExecutor;
import cn.icodening.collapse.core.util.VirtualThreadExecutorServiceProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author icodening
 * @date 2023.05.16
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnCollapseEnabled
public class CollapseExecutorAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(ListeningCollector.class)
    public ListeningCollector suspendableCollector(CollapseExecutorProperties collapseExecutorProperties) {
        SuspendableCollector suspendableCollector = new SuspendableCollector();
        suspendableCollector.setDuration(collapseExecutorProperties.getCollectingWaitTime());
        suspendableCollector.setThreshold(collapseExecutorProperties.getWaitThreshold());
        return suspendableCollector;
    }

    @Bean
    @ConditionalOnMissingBean(BlockingCallableGroupCollapseExecutor.class)
    public BlockingCallableGroupCollapseExecutor blockingCallableGroupCollapseExecutor(ListeningCollector listeningCollector) {
        return new BlockingCallableGroupCollapseExecutor(listeningCollector);
    }

    @Bean
    @ConditionalOnMissingBean(AsyncCallableGroupCollapseExecutor.class)
    public AsyncCallableGroupCollapseExecutor asyncCallableGroupCollapseExecutor(ListeningCollector listeningCollector, @Qualifier("collapseExecutorService") ExecutorService executorService) {
        AsyncCallableGroupCollapseExecutor collapseExecutor = new AsyncCallableGroupCollapseExecutor(listeningCollector);
        collapseExecutor.setExecutor(executorService);
        return collapseExecutor;
    }

    @Bean
    @ConditionalOnMissingBean(FutureCallableGroupCollapseExecutor.class)
    public FutureCallableGroupCollapseExecutor futureCallableGroupCollapseExecutor(ListeningCollector listeningCollector) {
        return new FutureCallableGroupCollapseExecutor(listeningCollector);
    }

    @Bean
    @ConditionalOnMissingBean(name = "collapseExecutorService")
    public ExecutorService collapseExecutorService(CollapseExecutorProperties collapseExecutorProperties) {
        CollapseExecutorProperties.ThreadPool.Virtual virtual = collapseExecutorProperties.getThreadPool().getVirtual();
        if (virtual.isEnabled()) {
            ExecutorService executorService = VirtualThreadExecutorServiceProvider.tryInstantiateVitrualThreadExecutorService();
            if (executorService != null) {
                return executorService;
            }
        }
        CollapseExecutorProperties.ThreadPool.Platform threadPool = collapseExecutorProperties.getThreadPool().getPlatform();
        int corePoolSize = threadPool.getCorePoolSize();
        int maximumPoolSize = threadPool.getMaximumPoolSize();
        int queueSize = threadPool.getQueueSize();
        int keepAliveTime = threadPool.getKeepAliveTime();
        String prefix = threadPool.getPrefix();
        BlockingQueue<Runnable> queue = queueSize == 0 ? new SynchronousQueue<>() : new LinkedBlockingQueue<>(queueSize);
        return new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.MILLISECONDS, queue, new ThreadFactory() {

            private final AtomicInteger INCREMENT = new AtomicInteger(1);

            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r);
                thread.setName(prefix + "-" + INCREMENT.getAndIncrement());
                thread.setDaemon(true);
                return thread;
            }
        });
    }
}
