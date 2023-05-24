package cn.icodening.tools.collapse.spring.boot.configuration;

import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author icodening
 * @date 2023.05.24
 */
@ConfigurationPropertiesScan("cn.icodening.tools.collapse.spring.boot")
@ComponentScan("cn.icodening.tools.collapse.spring.boot")
class CollapseComponentScanner {
}
