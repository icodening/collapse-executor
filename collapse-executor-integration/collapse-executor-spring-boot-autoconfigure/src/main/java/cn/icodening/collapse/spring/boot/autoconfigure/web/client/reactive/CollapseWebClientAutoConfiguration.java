package cn.icodening.collapse.spring.boot.autoconfigure.web.client.reactive;

import cn.icodening.collapse.core.ListeningCollector;
import cn.icodening.collapse.core.support.FutureCallableGroupCollapseExecutor;
import cn.icodening.collapse.spring.boot.autoconfigure.ConditionalOnCollapseEnabled;
import cn.icodening.collapse.spring.boot.http.reactive.CollapseExchangeFilterFunction;
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
        CollapseExchangeFilterFunction collapseExchangeFilterFunction = new CollapseExchangeFilterFunction();
        collapseExchangeFilterFunction.setCollapseGroupResolver(new ConfigurationCollapseGroupResolver(collapseWebClientProperties));
        collapseExchangeFilterFunction.setFutureCallableGroupCollapseExecutor(new FutureCallableGroupCollapseExecutor(listeningCollector));
        return collapseExchangeFilterFunction;
    }

}
