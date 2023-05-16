package cn.icodening.tools.collapse.core;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.locks.LockSupport;

/**
 * @author icodening
 * @date 2023.05.14
 */
public class ThreadlessExecutor implements Executor {

    private static final Object SHUTDOWN = new Object();

    private final Queue<Runnable> queue = new ConcurrentLinkedQueue<>();

    private volatile Object waiter;

    public void waitAndDrain() throws InterruptedException {
        throwIfInterrupted();
        Runnable runnable = queue.poll();
        if (runnable == null) {
            waiter = Thread.currentThread();
            try {
                while ((runnable = queue.poll()) == null) {
                    LockSupport.park(this);
                    throwIfInterrupted();
                }
            } finally {
                waiter = null;
            }
        }
        do {
            runnable.run();
        } while ((runnable = queue.poll()) != null);
    }

    private static void throwIfInterrupted() throws InterruptedException {
        if (Thread.interrupted()) {
            throw new InterruptedException();
        }
    }

    @Override
    public void execute(Runnable runnable) {
        queue.add(runnable);
        if (waiter != SHUTDOWN) {
            LockSupport.unpark((Thread) waiter);
        } else if (queue.remove(runnable)) {
            throw new RejectedExecutionException();
        }
    }

    public void shutdown() {
        waiter = SHUTDOWN;
        Runnable runnable;
        while ((runnable = queue.poll()) != null) {
            runnable.run();
        }
    }
}
