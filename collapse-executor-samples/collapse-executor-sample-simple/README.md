# collapse-executor-sample-simple 
该模块为折叠执行器简单使用案例

### 1.同步阻塞调用

[BlockingCollapseExecutorExample.java](src/main/java/com/icodening/collapse/sample/simple/BlockingCollapseExecutorExample.java)

````java
public class BlockingCollapseExecutorExample {

    public static void main(String[] args) throws Throwable {
        BlockingCallableGroupCollapseExecutor blockingCollapseExecutor = new BlockingCallableGroupCollapseExecutor();
        String outputString = blockingCollapseExecutor.execute("example group", () -> "Hello World Collapse Executor. Blocking");
        System.out.println(outputString);
    }
}
````

### 2.异步调用

[AsyncCollapseExecutorExample.java](src/main/java/com/icodening/collapse/sample/simple/AsyncCollapseExecutorExample.java)

````java
public class AsyncCollapseExecutorExample {

    public static void main(String[] args) throws Throwable {
        AsyncCallableGroupCollapseExecutor asyncCallableGroupCollapseExecutor = new AsyncCallableGroupCollapseExecutor();
        asyncCallableGroupCollapseExecutor.setExecutor(new ThreadPoolExecutor(10, 10, 0, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(), r -> {
            Thread thread = new Thread(r);
            thread.setDaemon(true);
            return thread;
        }));
        asyncCallableGroupCollapseExecutor.execute("example group", () -> "Hello World Collapse Executor. Async")
                .thenAccept(System.out::println)
                .thenRun(() -> System.exit(0));
        System.in.read();
    }
}
````

### 3.非阻塞异步调用

[FutureCollapseExecutorExample.java](src/main/java/com/icodening/collapse/sample/simple/FutureCollapseExecutorExample.java)
> 这种方式必须保证Callable中的处理逻辑是非阻塞的！！！

````java
public class FutureCollapseExecutorExample {

    public static void main(String[] args) throws Throwable {
        FutureCallableGroupCollapseExecutor futureCollapseExecutor = new FutureCallableGroupCollapseExecutor();
        futureCollapseExecutor.execute("example group", () -> CompletableFuture.completedFuture("Hello World Collapse Executor. Future"))
                .thenAccept(System.out::println)
                .thenRun(() -> System.exit(0));
        System.in.read();
    }
}
````