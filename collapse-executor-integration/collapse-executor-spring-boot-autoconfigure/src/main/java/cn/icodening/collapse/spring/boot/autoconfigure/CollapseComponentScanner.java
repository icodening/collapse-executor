package cn.icodening.collapse.spring.boot.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author icodening
 * @date 2023.05.24
 */
@ConfigurationPropertiesScan(basePackageClasses = ComponentScanMark.class)
@ComponentScan(basePackageClasses = ComponentScanMark.class)
class CollapseComponentScanner {
}
