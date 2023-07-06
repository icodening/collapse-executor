package cn.icodening.collapse.aop;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;

/**
 * @author icodening
 * @date 2023.07.07
 */
public class AopCallableGroup {

    private final Class<?> declaringClass;

    private final Method method;

    private final Object[] arguments;

    private final Object target;

    public AopCallableGroup(Method method, Object[] arguments, Object target) {
        this.declaringClass = method.getDeclaringClass();
        this.method = method;
        this.arguments = arguments;
        this.target = target;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AopCallableGroup that = (AopCallableGroup) o;
        return declaringClass.equals(that.declaringClass) && method.equals(that.method) && Arrays.equals(arguments, that.arguments) && target.equals(that.target);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(declaringClass, method, target);
        result = 31 * result + Arrays.hashCode(arguments);
        return result;
    }
}
