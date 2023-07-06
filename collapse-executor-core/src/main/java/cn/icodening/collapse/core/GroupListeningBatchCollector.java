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

    private final Map<K, CollectorListener<E>> listeners;

    private final Function<E, K> classifier;

    public GroupListeningBatchCollector(Function<E, K> classifier) {
        this(SingleThreadExecutor.SHARE, classifier, new ConcurrentHashMap<>());
    }

    public GroupListeningBatchCollector(Executor dispatcher,
                                        Function<E, K> classifier) {
        this(dispatcher, classifier, new ConcurrentHashMap<>());
    }

    public GroupListeningBatchCollector(Executor dispatcher,
                                        Function<E, K> classifier,
                                        Map<K, CollectorListener<E>> repository) {
        super(dispatcher);
        this.classifier = Objects.requireNonNull(classifier, "classifier must be not null.");
        this.listeners = Objects.requireNonNull(repository, "repository must be not null.");
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
