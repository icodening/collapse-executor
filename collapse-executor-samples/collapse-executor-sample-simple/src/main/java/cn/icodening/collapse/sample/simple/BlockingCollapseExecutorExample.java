package cn.icodening.collapse.sample.simple;

import cn.icodening.collapse.core.support.BlockingCallableGroupCollapseExecutor;

/**
 * @author icodening
 * @date 2023.06.20
 */
public class BlockingCollapseExecutorExample {

    public static void main(String[] args) throws Throwable {
        BlockingCallableGroupCollapseExecutor blockingCollapseExecutor = new BlockingCallableGroupCollapseExecutor();
        String outputString = blockingCollapseExecutor.execute("example group", () -> "Hello World Collapse Executor. Blocking");
        System.out.println(outputString);
    }
}
