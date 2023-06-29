package cn.icodening.collapse.spring.boot.http.client;

import cn.icodening.collapse.spring.boot.pattern.CollapseDefinitionProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author icodening
 * @date 2023.06.29
 */
@ConfigurationProperties(prefix = "collapse.executor.rest-template")
public class CollapseRestTemplateProperties extends CollapseDefinitionProperties {

}
