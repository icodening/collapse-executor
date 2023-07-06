package cn.icodening.collapse.aop;

import cn.icodening.collapse.core.support.BlockingCallableGroupCollapseExecutor;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import java.lang.reflect.Method;

/**
 * @author icodening
 * @date 2023.07.06
 */
public class CollapseMethodInterceptor implements MethodInterceptor {

    private BlockingCallableGroupCollapseExecutor collapseExecutor;

    public CollapseMethodInterceptor() {
    }

    public CollapseMethodInterceptor(BlockingCallableGroupCollapseExecutor collapseExecutor) {
        this.collapseExecutor = collapseExecutor;
    }

    public void setCollapseExecutor(BlockingCallableGroupCollapseExecutor collapseExecutor) {
        this.collapseExecutor = collapseExecutor;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        BlockingCallableGroupCollapseExecutor collapseExecutor = this.collapseExecutor;
        if (collapseExecutor == null) {
            return invocation.proceed();
        }
        Method method = invocation.getMethod();
        Object[] arguments = invocation.getArguments();
        Object target = invocation.getThis();
        AopCallableGroup group = new AopCallableGroup(method, arguments, target);
        return collapseExecutor.execute(group, invocation::proceed);
    }
}
