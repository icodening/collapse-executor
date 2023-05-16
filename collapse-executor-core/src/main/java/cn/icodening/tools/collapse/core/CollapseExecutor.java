package cn.icodening.tools.collapse.core;

/**
 * @author icodening
 * @date 2023.05.14
 */
public interface CollapseExecutor<INPUT, OUTPUT> {

    OUTPUT execute(INPUT input) throws Throwable;

}
