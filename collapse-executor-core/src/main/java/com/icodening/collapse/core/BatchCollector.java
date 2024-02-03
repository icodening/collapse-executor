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

import java.io.Closeable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author icodening
 * @date 2023.05.14
 */
public abstract class BatchCollector<E> implements Closeable {

    private final Queue<E> queue;

    private final Executor dispatcher;

    private final AtomicBoolean processing = new AtomicBoolean(false);

    private volatile boolean close = false;

    public BatchCollector() {
        this(new SingleThreadExecutor());
    }

    public BatchCollector(Executor dispatcher) {
        this(dispatcher, new ConcurrentLinkedQueue<>());
    }

    public BatchCollector(Executor dispatcher, Queue<E> queue) {
        this.dispatcher = Objects.requireNonNull(dispatcher, "dispatcher must be not null.");
        this.queue = Objects.requireNonNull(queue, "queue must be not null.");
    }

    public final void enqueue(E element) {
        checkState();
        this.queue.add(element);
        schedule();
    }

    private void schedule() {
        if (processing.compareAndSet(false, true)) {
            try {
                dispatcher.execute(this::doDispatch);
            } catch (RejectedExecutionException | NullPointerException e) {
                processing.compareAndSet(true, false);
                throw e;
            }
        }
    }

    private void checkState() {
        if (this.close) {
            throw new UnsupportedOperationException("Collector has been closed.");
        }
    }

    @Override
    public final void close() {
        this.close = true;
        onClosed();
    }

    protected void onClosed() {

    }

    private void doDispatch() {
        try {
            preparedCollect();
            Itr<E> itr = new Itr<>(this.queue);
            Collection<E> collection = onCollecting(itr);
            onCollected(collection);
        } finally {
            processing.set(false);
            if (!queue.isEmpty()) {
                schedule();
            }
        }
    }

    protected void preparedCollect() {

    }

    protected Collection<E> onCollecting(Iterator<E> iterator) {
        List<E> elements = new ArrayList<>();
        while (iterator.hasNext()) {
            E next = iterator.next();
            elements.add(next);
        }
        return elements;
    }

    protected abstract void onCollected(Collection<E> elements);

    private static class Itr<T> implements Iterator<T> {

        private final Queue<T> queue;

        private boolean hasNext = true;

        private T next;

        private Itr(Queue<T> queue) {
            this.queue = queue;
        }

        @Override
        public boolean hasNext() {
            if (!hasNext) {
                return false;
            }
            this.next = queue.poll();
            this.hasNext = (next != null);
            return hasNext;
        }

        @Override
        public T next() {
            if (!hasNext) {
                throw new NoSuchElementException();
            }
            return next;
        }
    }
}
