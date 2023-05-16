package cn.icodening.tools.collapse.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * @author icodening
 * @date 2023.05.14
 */
public abstract class GroupListeningBatchCollector<K, E> extends BatchCollector<E> {

    private final Map<K, Consumer<Collection<E>>> listeners;

    private final Function<E, K> classifier;

    public GroupListeningBatchCollector(Executor dispatcher,
                                        Function<E, K> classifier,
                                        Map<K, Consumer<Collection<E>>> repository) {
        super(dispatcher);
        this.classifier = Objects.requireNonNull(classifier, "classifier must be not null.");
        this.listeners = Objects.requireNonNull(repository, "repository must be not null.");
    }

    public void addListener(K key, Consumer<Collection<E>> consumer) {
        this.listeners.put(key, consumer);
    }

    public void removeListener(K key) {
        this.listeners.remove(key);
    }

    Map<K, Consumer<Collection<E>>> getListeners() {
        return listeners;
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
            Consumer<Collection<E>> listener = this.listeners.get(key);
            listener.accept(collection);
        }
    }
}
