package cn.icodening.collapse.core;

import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author icodening
 * @date 2023.05.14
 */
public class SingleThreadExecutor implements Executor {

    private static final ThreadFactory THREAD_FACTORY = new InternalThreadFactory();

    private final ExecutorService delegate;

    private volatile boolean closed = false;

    public SingleThreadExecutor() {
        this.delegate = Executors.newSingleThreadExecutor(THREAD_FACTORY);
    }

    @Override
    public void execute(Runnable command) {
        Objects.requireNonNull(command, "command must be not null.");
        if (closed) {
            throw new RejectedExecutionException("SingleThreadExecutor has been shutdown.");
        }
        this.delegate.execute(command);
    }

    public void shutdown() {
        if (closed) {
            return;
        }
        this.closed = true;
        this.delegate.shutdown();
    }

    private static class InternalThreadFactory implements ThreadFactory {

        private static final AtomicInteger COUNT = new AtomicInteger(0);

        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r);
            thread.setName("SingleEventLoop-" + COUNT.incrementAndGet());
            thread.setDaemon(true);
            return thread;
        }
    }
}
