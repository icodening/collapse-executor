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
package com.icodening.collapse.aop;

import com.icodening.collapse.core.support.BlockingCallableGroupCollapseExecutor;
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
