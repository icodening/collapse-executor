package cn.icodening.collapse.spring.boot.autoconfigure.web.client;

import cn.icodening.collapse.core.ListeningCollector;
import cn.icodening.collapse.core.support.BlockingCallableGroupCollapseExecutor;
import cn.icodening.collapse.spring.boot.autoconfigure.ConditionalOnCollapseEnabled;
import cn.icodening.collapse.spring.web.client.CollapseHttpRequestInterceptor;
import cn.icodening.collapse.spring.web.pattern.PathPatternCollapseGroupResolver;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * @author icodening
 * @date 2023.05.25
 */
@ConditionalOnClass(RestTemplate.class)
@ConditionalOnCollapseEnabled
@AutoConfiguration
public class CollapseRestTemplateAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(CollapseHttpRequestInterceptor.class)
    public CollapseHttpRequestInterceptor collapseHttpRequestInterceptor(ListeningCollector listeningCollector,
                                                                         CollapseRestTemplateProperties collapseRestTemplateProperties) {
        CollapseHttpRequestInterceptor collapseHttpRequestInterceptor = new CollapseHttpRequestInterceptor();
        collapseHttpRequestInterceptor.setCollapseGroupResolver(new PathPatternCollapseGroupResolver(collapseRestTemplateProperties));
        collapseHttpRequestInterceptor.setBlockingCallableGroupCollapseExecutor(new BlockingCallableGroupCollapseExecutor(listeningCollector));
        return collapseHttpRequestInterceptor;
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

