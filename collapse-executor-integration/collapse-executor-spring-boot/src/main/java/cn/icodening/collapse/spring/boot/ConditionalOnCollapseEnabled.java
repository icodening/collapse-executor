package cn.icodening.collapse.spring.boot;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author icodening
 * @date 2023.05.25
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@ConditionalOnProperty(prefix = "collapse.executor", name = "enabled", havingValue = "true", matchIfMissing = true)
public @interface ConditionalOnCollapseEnabled {
}
