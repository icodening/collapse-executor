package cn.icodening.tools.collapse.spring.boot.configuration;

import cn.icodening.tools.collapse.core.ListeningBundleCollector;
import cn.icodening.tools.collapse.core.SingleThreadExecutor;
import cn.icodening.tools.collapse.core.SuspendableListeningBundleCollector;
import cn.icodening.tools.collapse.core.support.AsyncCallableGroupCollapseExecutor;
import cn.icodening.tools.collapse.core.support.CallableGroupCollapseExecutor;
import cn.icodening.tools.collapse.spring.boot.ConditionalOnCollapseEnabled;
import cn.icodening.tools.collapse.spring.boot.web.client.CollapseHttpRequestInterceptor;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
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
@Configuration
@ConditionalOnCollapseEnabled
public class CollapseExecutorAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(SingleThreadExecutor.class)
    public SingleThreadExecutor singleThreadExecutor() {
        return new SingleThreadExecutor();
    }

    @Bean
    @ConditionalOnMissingBean(ListeningBundleCollector.class)
    public ListeningBundleCollector suspendableListeningBundleCollector(SingleThreadExecutor singleThreadExecutor, CollapseExecutorProperties collapseExecutorProperties) {
        SuspendableListeningBundleCollector suspendableListeningBundleCollector = new SuspendableListeningBundleCollector(singleThreadExecutor);
        suspendableListeningBundleCollector.setDuration(collapseExecutorProperties.getCollectingWaitTime());
        suspendableListeningBundleCollector.setThreshold(collapseExecutorProperties.getWaitThreshold());
        return suspendableListeningBundleCollector;
    }

    @Bean
    @ConditionalOnMissingBean(CallableGroupCollapseExecutor.class)
    public CallableGroupCollapseExecutor callableGroupCollapseExecutor(ListeningBundleCollector listeningBundleCollector) {
        return new CallableGroupCollapseExecutor(listeningBundleCollector);
    }

    @Bean
    @ConditionalOnMissingBean(AsyncCallableGroupCollapseExecutor.class)
    public AsyncCallableGroupCollapseExecutor asyncCallableGroupCollapseExecutor(ListeningBundleCollector listeningBundleCollector, CollapseExecutorProperties collapseExecutorProperties) {
        AsyncCallableGroupCollapseExecutor collapseExecutor = new AsyncCallableGroupCollapseExecutor(listeningBundleCollector);
        collapseExecutor.setExecutor(collapseExecutorService(collapseExecutorProperties));
        return collapseExecutor;
    }

    @Bean
    @ConditionalOnMissingBean(name = "collapseExecutorService")
    public ExecutorService collapseExecutorService(CollapseExecutorProperties collapseExecutorProperties) {
        CollapseExecutorProperties.ThreadPool threadPool = collapseExecutorProperties.getThreadPool();
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

    @ConditionalOnClass(RestTemplate.class)
    @Configuration
    static class RestTemplateCollapseAutoConfiguration {

        @Bean
        @ConditionalOnMissingBean(CollapseHttpRequestInterceptor.class)
        public CollapseHttpRequestInterceptor collapseHttpRequestInterceptor(ListeningBundleCollector listeningBundleCollector) {
            return new CollapseHttpRequestInterceptor(listeningBundleCollector);
        }

        @Bean
        public SmartInitializingSingleton restTemplateCollapseCustomizer(CollapseHttpRequestInterceptor collapseHttpRequestInterceptor, ApplicationContext context) {
            return () -> {
                for (RestTemplate restTemplate : context.getBeansOfType(RestTemplate.class).values()) {
                    if (restTemplate == null) {
                        continue;
                    }
                    List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>(restTemplate.getInterceptors());
                    interceptors.add(collapseHttpRequestInterceptor);
                    restTemplate.setInterceptors(interceptors);
                }
            };
        }
    }


}
