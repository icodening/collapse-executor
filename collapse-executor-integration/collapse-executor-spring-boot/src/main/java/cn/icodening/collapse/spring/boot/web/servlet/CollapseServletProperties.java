package cn.icodening.collapse.spring.boot.web.servlet;

import cn.icodening.collapse.spring.boot.pattern.CollapseDefinitionProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author icodening
 * @date 2023.05.23
 */
@ConfigurationProperties(prefix = "collapse.executor.servlet")
public class CollapseServletProperties extends CollapseDefinitionProperties {

}
