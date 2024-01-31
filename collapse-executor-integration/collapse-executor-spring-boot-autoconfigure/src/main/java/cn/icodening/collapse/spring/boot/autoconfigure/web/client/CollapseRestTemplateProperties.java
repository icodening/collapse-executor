package cn.icodening.collapse.spring.boot.autoconfigure.web.client;

import cn.icodening.collapse.web.pattern.CollapseDefinitionProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * @author icodening
 * @date 2023.06.29
 */
@ConfigurationProperties(prefix = "collapse.executor.rest-template")
public class CollapseRestTemplateProperties extends CollapseDefinitionProperties {

    /**
     * Specify the RestTemplate bean that needs to take effect
     */
    private List<String> applyBeanNames = new ArrayList<>();

    public List<String> getApplyBeanNames() {
        return applyBeanNames;
    }

    public void setApplyBeanNames(List<String> applyBeanNames) {
        this.applyBeanNames = applyBeanNames;
    }
}
