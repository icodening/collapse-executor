package cn.icodening.collapse.spring.boot.web.servlet;

import cn.icodening.collapse.core.EqualsInputGrouper;
import cn.icodening.collapse.core.LengthLimitedInputGrouper;
import cn.icodening.collapse.core.ListeningCollector;
import cn.icodening.collapse.spring.boot.ConditionalOnCollapseEnabled;
import cn.icodening.collapse.spring.boot.pattern.ConfigurationCollapseGroupResolver;
import io.undertow.Undertow;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.embedded.undertow.UndertowWebServer;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.util.ReflectionUtils;

import javax.servlet.Filter;
import java.lang.reflect.Field;
import java.util.concurrent.ExecutorService;

/**
 * @author icodening
 * @date 2023.05.24
 */
@AutoConfiguration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnCollapseEnabled
@ConditionalOnProperty(prefix = "collapse.executor.servlet", name = "enabled", havingValue = "true", matchIfMissing = false)
public class CollapseServletAutoConfiguration {

    @Bean
    public FilterRegistrationBean<Filter> collapseHttpRequestServletFilterRegistrationBean(CollapseHttpRequestServletFilter collapseHttpRequestServletFilter) {
        return new FilterRegistrationBean<>(collapseHttpRequestServletFilter);
    }

    @Bean
    public CollapseHttpRequestServletFilter collapseHttpRequestServletFilter(AsyncServletExecutor asyncServletExecutor, CollapseServletProperties collapseServletProperties) {
        ConfigurationCollapseGroupResolver configurationCollapseGroupResolver = new ConfigurationCollapseGroupResolver(collapseServletProperties);
        return new CollapseHttpRequestServletFilter(asyncServletExecutor, configurationCollapseGroupResolver);
    }

    @Bean
    public AsyncServletExecutor asyncServletExecutor(ListeningCollector listeningCollector, ExecutorService collapseExecutorService, CollapseServletProperties collapseServletProperties) {
        AsyncServletExecutor asyncServletExecutor = new AsyncServletExecutor(listeningCollector);
        asyncServletExecutor.setExecutor(collapseExecutorService);
        asyncServletExecutor.setInputGrouper(LengthLimitedInputGrouper.newInstance(collapseServletProperties.getBatchSize(), EqualsInputGrouper.getInstance()));
        return asyncServletExecutor;
    }

    @ConditionalOnClass(name = "org.apache.catalina.startup.Tomcat")
    static class CollapseTomcatServerConfiguration {

        @Bean
        public WebServerFactoryCustomizer<TomcatServletWebServerFactory> collapseTomcatWebServerCustomizer(AsyncServletExecutor asyncServletExecutor) {
            return factory -> factory.addProtocolHandlerCustomizers(protocolHandler -> asyncServletExecutor.setExecutorSupplier(protocolHandler::getExecutor));
        }
    }

    @ConditionalOnClass(name = "io.undertow.Undertow")
    static class CollapseUndertowServerConfiguration {

        @Bean
        public SmartInitializingSingleton collapseUndertowWebServerCustomizer(ServletWebServerApplicationContext servletWebServerApplicationContext, AsyncServletExecutor asyncServletExecutor) {


            return () -> {
                Field field = ReflectionUtils.findField(UndertowWebServer.class, "undertow");
                if (field == null) {
                    return;
                }
                asyncServletExecutor.setExecutorSupplier(() -> {
                    try {
                        UndertowWebServer undertowWebServer = (UndertowWebServer) servletWebServerApplicationContext.getWebServer();
                        ReflectionUtils.makeAccessible(field);
                        Undertow undertow = (Undertow) ReflectionUtils.getField(field, undertowWebServer);
                        //not null at runtime
                        assert undertow != null;
                        return undertow.getWorker();
                    } catch (Throwable throwable) {
                        //no op
                        throw new RuntimeException(throwable);
                    } finally {
                        field.setAccessible(false);
                    }
                });
            };
        }
    }
}
