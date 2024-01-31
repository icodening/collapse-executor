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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * @author icodening
 * @date 2023.05.14
 */
public abstract class GroupListeningBatchCollector<K, E> extends BatchCollector<E> {

    private final Map<K, CollectorListener<E>> listeners = new ConcurrentHashMap<>(256);

    private final Function<E, K> classifier;

    public GroupListeningBatchCollector(Function<E, K> classifier) {
        super();
        this.classifier = Objects.requireNonNull(classifier, "classifier must be not null.");
    }

    public GroupListeningBatchCollector(Executor dispatcher,
                                        Function<E, K> classifier) {
        super(dispatcher);
        this.classifier = Objects.requireNonNull(classifier, "classifier must be not null.");
    }

    public void addListener(K key, CollectorListener<E> listener) {
        this.listeners.put(key, listener);
    }

    public void removeListener(K key) {
        this.listeners.remove(key);
    }

    @Override
    protected void onCollected(Collection<E> elements) {
        Map<K, Collection<E>> groups = elements.stream()
                .collect(Collectors.groupingBy(
                        classifier,
                        Collectors.toCollection((Supplier<Collection<E>>) ArrayList::new)
                ));
        for (Map.Entry<K, Collection<E>> entry : groups.entrySet()) {
            K key = entry.getKey();
            Collection<E> collection = entry.getValue();
            CollectorListener<E> listener = this.listeners.get(key);
            listener.onCollected(collection);
        }
    }
}
