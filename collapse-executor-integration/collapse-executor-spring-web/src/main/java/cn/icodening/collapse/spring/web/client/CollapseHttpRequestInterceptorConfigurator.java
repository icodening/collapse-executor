package cn.icodening.collapse.spring.web.client;

import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author icodening
 * @date 2023.10.26
 */
public class CollapseHttpRequestInterceptorConfigurator {

    private final CollapseHttpRequestInterceptor collapseHttpRequestInterceptor;

    public CollapseHttpRequestInterceptorConfigurator(CollapseHttpRequestInterceptor collapseHttpRequestInterceptor) {
        Assert.notNull(collapseHttpRequestInterceptor, "collapseHttpRequestInterceptor must be not null.");
        this.collapseHttpRequestInterceptor = collapseHttpRequestInterceptor;
    }

    public void configurer(RestTemplate... restTemplate) {
        configurer(Arrays.asList(restTemplate));
    }

    public void configurer(List<RestTemplate> restTemplates) {
        doConfigurer(restTemplates, collapseHttpRequestInterceptor);
    }

    private void doConfigurer(List<RestTemplate> restTemplates,
                              CollapseHttpRequestInterceptor collapseHttpRequestInterceptor) {
        for (RestTemplate restTemplate : restTemplates) {
            List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>(restTemplate.getInterceptors());
            interceptors.add(collapseHttpRequestInterceptor);
            restTemplate.setInterceptors(interceptors);
        }
    }
}
