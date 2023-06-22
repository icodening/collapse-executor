package cn.icodening.collapse.spring.boot.web.reactive;

import cn.icodening.collapse.core.ListenableCollector;
import cn.icodening.collapse.spring.boot.ConditionalOnCollapseEnabled;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * @author icodening
 * @date 2023.06.23
 */
@AutoConfiguration
@ConditionalOnCollapseEnabled
@ConditionalOnClass(WebClient.class)
public class CollapseWebClientAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(CollapseExchangeFilterFunction.class)
    public CollapseExchangeFilterFunction collapseHttpRequestInterceptor(ListenableCollector listenableCollector) {
        return new CollapseExchangeFilterFunction(listenableCollector);
    }

}
