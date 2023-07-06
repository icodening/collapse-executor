package cn.icodening.collapse.spring.boot.aop;

import cn.icodening.collapse.aop.CollapseMethodInterceptor;
import cn.icodening.collapse.aop.CollapsibleAnnotationAspect;
import cn.icodening.collapse.core.support.BlockingCallableGroupCollapseExecutor;
import cn.icodening.collapse.spring.boot.ConditionalOnCollapseEnabled;
import org.aopalliance.intercept.MethodInterceptor;
import org.aspectj.lang.JoinPoint;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;

/**
 * @author icodening
 * @date 2023.07.07
 */
@AutoConfiguration
@ConditionalOnCollapseEnabled
public class CollapseAopAutoConfiguration {

    @ConditionalOnClass({JoinPoint.class, CollapsibleAnnotationAspect.class})
    static class AspectAutoConfiguration {

        @Bean
        public CollapsibleAnnotationAspect collapsibleAnnotationAspect(BlockingCallableGroupCollapseExecutor collapseExecutor) {
            return new CollapsibleAnnotationAspect(collapseExecutor);
        }
    }

    @ConditionalOnClass({MethodInterceptor.class, CollapseMethodInterceptor.class})
    static class AopallianceAutoConfiguration {

        @Bean
        public CollapseMethodInterceptor collapseMethodInterceptor(BlockingCallableGroupCollapseExecutor collapseExecutor) {
            return new CollapseMethodInterceptor(collapseExecutor);
        }
    }
}
