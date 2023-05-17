package cn.icodening.tools.collapse.spring.boot.configuration;

import cn.icodening.tools.collapse.core.CallableGroupCollapseExecutor;
import cn.icodening.tools.collapse.core.FixedPauseTimeListeningBundleCollector;
import cn.icodening.tools.collapse.core.SingleThreadExecutor;
import cn.icodening.tools.collapse.spring.boot.web.client.CollapseHttpRequestInterceptor;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * @author icodening
 * @date 2023.05.16
 */
@Configuration
@EnableConfigurationProperties(CollapseExecutorProperties.class)
@ConditionalOnProperty(prefix = "collapse.executor", name = "enabled", havingValue = "true", matchIfMissing = true)
public class CollapseExecutorAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(SingleThreadExecutor.class)
    public SingleThreadExecutor singleThreadExecutor() {
        return new SingleThreadExecutor();
    }

    @Bean
    @ConditionalOnMissingBean(FixedPauseTimeListeningBundleCollector.class)
    public FixedPauseTimeListeningBundleCollector fixedPauseTimeListeningBundleCollector(SingleThreadExecutor singleThreadExecutor, CollapseExecutorProperties collapseExecutorProperties) {
        long collectingWaitTime = collapseExecutorProperties.getCollectingWaitTime();
        return new FixedPauseTimeListeningBundleCollector(singleThreadExecutor, collectingWaitTime);
    }

    @Bean
    @ConditionalOnMissingBean(CallableGroupCollapseExecutor.class)
    public CallableGroupCollapseExecutor callableGroupCollapseExecutor(FixedPauseTimeListeningBundleCollector fixedPauseTimeListeningBundleCollector) {
        return new CallableGroupCollapseExecutor(fixedPauseTimeListeningBundleCollector);
    }


    @ConditionalOnClass(RestTemplate.class)
    @Configuration
    static class RestTemplateCollapseAutoConfiguration {

        @Bean
        @ConditionalOnMissingBean(CollapseHttpRequestInterceptor.class)
        public CollapseHttpRequestInterceptor collapseHttpRequestInterceptor(FixedPauseTimeListeningBundleCollector fixedPauseTimeListeningBundleCollector) {
            return new CollapseHttpRequestInterceptor(fixedPauseTimeListeningBundleCollector);
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
