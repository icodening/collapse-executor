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
package com.icodening.collapse.core;

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
