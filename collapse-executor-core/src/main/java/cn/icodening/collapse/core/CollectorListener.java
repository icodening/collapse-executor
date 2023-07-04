package cn.icodening.collapse.core;

import java.util.Collection;

/**
 * @author icodening
 * @date 2023.07.04
 * @see GroupListeningBatchCollector
 */
@FunctionalInterface
public interface CollectorListener<E> {

    void onCollected(Collection<E> collection);

}
