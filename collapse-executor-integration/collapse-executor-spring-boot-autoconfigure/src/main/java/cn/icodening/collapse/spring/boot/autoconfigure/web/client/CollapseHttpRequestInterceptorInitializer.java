package cn.icodening.collapse.spring.boot.autoconfigure.web.client;

import cn.icodening.collapse.spring.web.client.CollapseHttpRequestInterceptorConfigurator;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ApplicationObjectSupport;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * @author icodening
 * @date 2023.10.26
 */
public class CollapseHttpRequestInterceptorInitializer extends ApplicationObjectSupport implements SmartInitializingSingleton {

    private static final Logger LOGGER = Logger.getLogger(CollapseHttpRequestInterceptorInitializer.class.getName());

    private final CollapseHttpRequestInterceptorConfigurator collapseHttpRequestInterceptorConfigurator;

    private List<String> restTemplateBeanNames = Collections.emptyList();

    public CollapseHttpRequestInterceptorInitializer(CollapseHttpRequestInterceptorConfigurator collapseHttpRequestInterceptorConfigurator) {
        this.collapseHttpRequestInterceptorConfigurator = collapseHttpRequestInterceptorConfigurator;
    }

    public void setCandidateRestTemplateBeanNames(List<String> restTemplateBeanNames) {
        this.restTemplateBeanNames = restTemplateBeanNames;
    }

    @Override
    public void afterSingletonsInstantiated() {
        ApplicationContext applicationContext = obtainApplicationContext();
        Map<String, RestTemplate> restTemplates = applicationContext.getBeansOfType(RestTemplate.class);
        LOGGER.info("Found RestTemplate bean names. " + Arrays.toString(restTemplates.keySet().toArray(new String[0])));
        for (String restTemplateBeanName : restTemplateBeanNames) {
            RestTemplate restTemplate = restTemplates.get(restTemplateBeanName);
            if (restTemplate == null) {
                LOGGER.warning(String.format("[%s] RestTemplate not exists, will be ignore initialization.", restTemplateBeanName));
                continue;
            }
            collapseHttpRequestInterceptorConfigurator.configurer(restTemplate);
            LOGGER.info(String.format("RestTemplate [%s] configurer completed.", restTemplateBeanName));
        }
    }
}
