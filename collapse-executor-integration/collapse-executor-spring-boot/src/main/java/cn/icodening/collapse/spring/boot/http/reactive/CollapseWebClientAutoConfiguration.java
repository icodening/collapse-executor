package cn.icodening.collapse.spring.boot.http.reactive;

import cn.icodening.collapse.core.ListeningCollector;
import cn.icodening.collapse.spring.boot.ConditionalOnCollapseEnabled;
import cn.icodening.collapse.spring.boot.pattern.ConfigurationCollapseGroupResolver;
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
    public CollapseExchangeFilterFunction collapseExchangeFilterFunction(ListeningCollector listeningCollector,
                                                                         CollapseWebClientProperties collapseWebClientProperties) {
        CollapseExchangeFilterFunction collapseExchangeFilterFunction = new CollapseExchangeFilterFunction(listeningCollector);
        collapseExchangeFilterFunction.setCollapseGroupResolver(new ConfigurationCollapseGroupResolver(collapseWebClientProperties));
        return collapseExchangeFilterFunction;
    }

}
