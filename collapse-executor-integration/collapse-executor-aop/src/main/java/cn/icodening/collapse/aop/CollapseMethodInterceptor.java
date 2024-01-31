/*
 * Copyright 2023-2024 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
