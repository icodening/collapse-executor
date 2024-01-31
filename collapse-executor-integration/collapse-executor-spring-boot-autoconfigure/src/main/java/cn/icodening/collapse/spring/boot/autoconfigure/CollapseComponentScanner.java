package cn.icodening.collapse.spring.boot.autoconfigure;

import org.springframework.boot.autoconfigure.AutoConfigurationExcludeFilter;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

/**
 * @author icodening
 * @date 2023.05.24
 */
@ConfigurationPropertiesScan(basePackageClasses = ComponentScanMark.class)
@ComponentScan(basePackageClasses = ComponentScanMark.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.CUSTOM, classes = AutoConfigurationExcludeFilter.class))
class CollapseComponentScanner {
}
