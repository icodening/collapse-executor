package cn.icodening.collapse.spring.boot.http.reactive;

import cn.icodening.collapse.spring.boot.pattern.CollapseDefinitionProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author icodening
 * @date 2023.06.29
 */
@ConfigurationProperties(prefix = "collapse.executor.web-client")
public class CollapseWebClientProperties extends CollapseDefinitionProperties {

}
