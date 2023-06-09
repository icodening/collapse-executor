package cn.icodening.collapse.spring.boot.autoconfigure.web.client.reactive;

import cn.icodening.collapse.spring.web.pattern.CollapseDefinitionProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author icodening
 * @date 2023.06.29
 */
@ConfigurationProperties(prefix = "collapse.executor.web-client")
public class CollapseWebClientProperties extends CollapseDefinitionProperties {

}
