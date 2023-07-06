package cn.icodening.collapse.aop;

import cn.icodening.collapse.core.support.BlockingCallableGroupCollapseExecutor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;

/**
 * @author icodening
 * @date 2023.07.06
 */
@Aspect
public class CollapsibleAnnotationAspect {

    private BlockingCallableGroupCollapseExecutor collapseExecutor;

    public CollapsibleAnnotationAspect() {
    }

    public CollapsibleAnnotationAspect(BlockingCallableGroupCollapseExecutor collapseExecutor) {
        this.collapseExecutor = collapseExecutor;
    }

    public void setCollapseExecutor(BlockingCallableGroupCollapseExecutor collapseExecutor) {
        this.collapseExecutor = collapseExecutor;
    }

    @Pointcut(value = "@within(cn.icodening.collapse.aop.annotation.Collapsible) || @annotation(cn.icodening.collapse.aop.annotation.Collapsible)")
    public void collapsiblePointcut() {

    }

    @Around(value = "collapsiblePointcut()")
    public Object collapseExecute(ProceedingJoinPoint pjp) throws Throwable {
        BlockingCallableGroupCollapseExecutor collapseExecutor = this.collapseExecutor;
        if (collapseExecutor == null) {
            return pjp.proceed();
        }
        String kind = pjp.getKind();
        if (!JoinPoint.METHOD_EXECUTION.equals(kind)) {
            return pjp.proceed();
        }
        MethodSignature methodSignature = (MethodSignature) pjp.getSignature();
        Method method = methodSignature.getMethod();
        Object[] arguments = pjp.getArgs();
        AopCallableGroup group = new AopCallableGroup(method, arguments, pjp.getTarget());
        return collapseExecutor.execute(group, pjp::proceed);
    }
}
