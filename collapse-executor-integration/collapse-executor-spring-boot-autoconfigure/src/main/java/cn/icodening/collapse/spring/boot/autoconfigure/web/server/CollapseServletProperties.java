package cn.icodening.collapse.spring.boot.autoconfigure.web.server;

import cn.icodening.collapse.spring.web.pattern.CollapseDefinitionProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author icodening
 * @date 2023.05.23
 */
@ConfigurationProperties(prefix = "collapse.executor.servlet")
public class CollapseServletProperties extends CollapseDefinitionProperties {

}
