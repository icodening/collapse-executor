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
package cn.icodening.collapse.core;

import java.util.Objects;
import java.util.SortedSet;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

/**
 * @author icodening
 * @date 2023.05.14
 */
public class SingleThreadExecutor implements Executor {

    private final ExecutorService delegate;

    private volatile boolean closed = false;

    public SingleThreadExecutor() {
        this.delegate = DelegateExecutorServiceProvider.getDelegateExecutorService();
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

    private static class DelegateExecutorServiceProvider {

        private static final String THREAD_PREFIX = "SingleThreadExecutor";

        private static final ExecutorService DELEGATE_EXECUTOR_SERVICE;

        static {
            ExecutorService delegate = null;
            if (isJava21()) {
                delegate = initVirtualThreadExecutorService();
            }
            if (delegate == null) {
                delegate = initGenericExecutorService();
            }
            DELEGATE_EXECUTOR_SERVICE = delegate;
        }

        private static ExecutorService getDelegateExecutorService() {
            return DELEGATE_EXECUTOR_SERVICE;
        }

        private DelegateExecutorServiceProvider() {
        }

        private static boolean isJava21() {
            try {
                //'getFirst' since 21
                SortedSet.class.getDeclaredMethod("getFirst");
                return true;
            } catch (NoSuchMethodException e) {
                return false;
            }
        }

        private static ExecutorService initVirtualThreadExecutorService() {
            try {
                return (ExecutorService) Executors.class
                        .getDeclaredMethod("newVirtualThreadPerTaskExecutor")
                        .invoke(null);
            } catch (Throwable ignored) {
                Logger logger = Logger.getLogger(DelegateExecutorServiceProvider.class.getName());
                logger.warning("Current java version is 21+, but initialize 'VirtualThreadPerTaskExecutor' failed, will use default executor instead.");
            }
            return null;
        }

        private static ExecutorService initGenericExecutorService() {
            ThreadFactory threadFactory = new ThreadFactory() {

                private final AtomicInteger COUNT = new AtomicInteger(0);

                @Override
                public Thread newThread(Runnable r) {
                    Thread thread = new Thread(r);
                    thread.setName(THREAD_PREFIX + "-Platform-" + COUNT.incrementAndGet());
                    thread.setDaemon(true);
                    return thread;
                }
            };
            return Executors.newSingleThreadExecutor(threadFactory);
        }
    }
}


