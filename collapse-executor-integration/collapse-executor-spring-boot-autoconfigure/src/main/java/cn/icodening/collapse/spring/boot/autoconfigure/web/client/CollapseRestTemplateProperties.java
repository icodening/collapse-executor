package cn.icodening.collapse.spring.boot.autoconfigure.web.client;

import cn.icodening.collapse.spring.web.pattern.CollapseDefinitionProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author icodening
 * @date 2023.06.29
 */
@ConfigurationProperties(prefix = "collapse.executor.rest-template")
public class CollapseRestTemplateProperties extends CollapseDefinitionProperties {

}
