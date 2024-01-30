package cn.icodening.collapse.spring.boot.autoconfigure.web.client;

import cn.icodening.collapse.core.ListeningCollector;
import cn.icodening.collapse.core.support.BlockingCallableGroupCollapseExecutor;
import cn.icodening.collapse.spring.boot.autoconfigure.ConditionalOnCollapseEnabled;
import cn.icodening.collapse.spring.web.client.CollapseHttpRequestInterceptor;
import cn.icodening.collapse.spring.web.client.CollapseHttpRequestInterceptorConfigurator;
import cn.icodening.collapse.spring.web.pattern.PathPatternCollapseGroupResolver;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * @author icodening
 * @date 2023.05.25
 */
@ConditionalOnClass(RestTemplate.class)
@ConditionalOnCollapseEnabled
@Configuration(proxyBeanMethods = false)
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
    @ConditionalOnBean(CollapseHttpRequestInterceptor.class)
    public CollapseHttpRequestInterceptorConfigurator collapseHttpRequestInterceptorConfigurer(CollapseHttpRequestInterceptor collapseHttpRequestInterceptor) {
        return new CollapseHttpRequestInterceptorConfigurator(collapseHttpRequestInterceptor);
    }

    @Bean
    @ConditionalOnBean(CollapseHttpRequestInterceptorConfigurator.class)
    public CollapseHttpRequestInterceptorInitializer collapseHttpRequestInterceptorInitializer(CollapseRestTemplateProperties collapseRestTemplateProperties,
                                                                                               CollapseHttpRequestInterceptorConfigurator configurator) {
        List<String> applyBeans = collapseRestTemplateProperties.getApplyBeanNames();
        CollapseHttpRequestInterceptorInitializer initializer = new CollapseHttpRequestInterceptorInitializer(configurator);
        initializer.setCandidateRestTemplateBeanNames(applyBeans);
        return initializer;
    }

}

