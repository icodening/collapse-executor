package cn.icodening.collapse.spring.boot.configuration;

import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author icodening
 * @date 2023.05.24
 */
@ConfigurationPropertiesScan("cn.icodening.collapse.spring.boot")
@ComponentScan("cn.icodening.collapse.spring.boot")
class CollapseComponentScanner {
}
