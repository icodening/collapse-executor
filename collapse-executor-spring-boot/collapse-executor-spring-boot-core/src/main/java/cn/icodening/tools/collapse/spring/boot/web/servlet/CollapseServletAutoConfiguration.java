package cn.icodening.tools.collapse.spring.boot.web.servlet;

import cn.icodening.tools.collapse.core.EqualsInputGrouper;
import cn.icodening.tools.collapse.core.LengthLimitedInputGrouper;
import cn.icodening.tools.collapse.core.ListeningBundleCollector;
import cn.icodening.tools.collapse.spring.boot.ConditionalOnCollapseEnabled;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.servlet.Filter;
import javax.servlet.Servlet;
import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * @author icodening
 * @date 2023.05.24
 */
@Configuration
@ConditionalOnClass(Servlet.class)
@ConditionalOnCollapseEnabled
@ConditionalOnProperty(prefix = "collapse.executor.servlet", name = "enabled", havingValue = "true", matchIfMissing = false)
public class CollapseServletAutoConfiguration {

    @Bean
    public FilterRegistrationBean<Filter> collapseHttpRequestServletFilterRegistrationBean(CollapseHttpRequestServletFilter collapseHttpRequestServletFilter) {
        return new FilterRegistrationBean<>(collapseHttpRequestServletFilter);
    }

    @Bean
    public CollapseHttpRequestServletFilter collapseHttpRequestServletFilter(AsyncServletExecutor asyncServletExecutor,
                                                                             HttpServletRequestMatcher httpServletRequestMatcher) {
        return new CollapseHttpRequestServletFilter(asyncServletExecutor, httpServletRequestMatcher);
    }

    @Bean
    public AsyncServletExecutor asyncServletExecutor(ListeningBundleCollector listeningBundleCollector,
                                                     ExecutorService collapseExecutorService,
                                                     CollapseServletProperties collapseServletProperties) {
        AsyncServletExecutor asyncServletExecutor = new AsyncServletExecutor(listeningBundleCollector);
        asyncServletExecutor.setExecutor(collapseExecutorService);
        asyncServletExecutor.setInputGrouper(LengthLimitedInputGrouper.newInstance(collapseServletProperties.getBatchSize(), EqualsInputGrouper.getInstance()));
        return asyncServletExecutor;
    }

    @Bean
    public HttpServletRequestMatcher httpServletRequestMatcher(CollapseServletProperties collapseServletProperties) {
        return new ConfigurationRequestMatcher(collapseServletProperties);
    }

    @Bean
    @Primary
    public HttpServletRequestMatcher compositeHttpServletRequestMatcher(List<HttpServletRequestMatcher> matchers) {
        return new CompositeHttpServletRequestMatcher(matchers);
    }
}
