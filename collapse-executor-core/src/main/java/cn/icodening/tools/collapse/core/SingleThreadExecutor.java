package cn.icodening.tools.collapse.core;

import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.LockSupport;

/**
 * @author icodening
 * @date 2023.05.14
 */
public class SingleThreadExecutor implements Executor {

    private static final AtomicInteger COUNT = new AtomicInteger(0);

    private final InternalThread thread;

    private final AtomicBoolean started = new AtomicBoolean(false);

    private volatile boolean closed = false;

    public SingleThreadExecutor() {
        this.thread = new InternalThread();
    }

    @Override
    public void execute(Runnable command) {
        Objects.requireNonNull(command, "command must be not null.");
        if (closed) {
            throw new UnsupportedOperationException("SingleThreadExecutor has been shutdown.");
        }
        if (started.compareAndSet(false, true)) {
            this.thread.start();
        }
        this.thread.getQueue().add(command);
        wakeup();
    }

    private void wakeup() {
        if (this.thread.getProcessing().compareAndSet(false, true)) {
            LockSupport.unpark(thread);
        }
    }

    public void shutdown() {
        this.closed = true;
    }


    private class InternalThread extends Thread {

        private final Queue<Runnable> queue = new ConcurrentLinkedQueue<>();

        private final AtomicBoolean processing = new AtomicBoolean(false);

        private InternalThread() {
            super("SingleEventLoop-" + COUNT.incrementAndGet());
            this.setDaemon(true);
        }

        private Queue<Runnable> getQueue() {
            return queue;
        }

        private AtomicBoolean getProcessing() {
            return processing;
        }

        @Override
        public void run() {
            while (true) {
                if (Thread.currentThread().isInterrupted()) {
                    return;
                }
                if (queue.isEmpty()) {
                    LockSupport.park();
                }
                try {
                    Runnable r;
                    while ((r = queue.poll()) != null) {
                        r.run();
                    }
                } finally {
                    processing.set(false);
                    if (!queue.isEmpty()) {
                        SingleThreadExecutor.this.wakeup();
                    }
                }
            }
        }
    }
}
