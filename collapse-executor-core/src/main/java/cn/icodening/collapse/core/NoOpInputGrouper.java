package cn.icodening.collapse.core;

import java.util.Collection;
import java.util.Collections;

/**
 * @author icodening
 * @date 2023.05.14
 */
public class NoOpInputGrouper implements InputGrouper<Object> {

    private static final InputGrouper<?> INSTANCE = new NoOpInputGrouper();

    @SuppressWarnings("unchecked")
    public static <INPUT> InputGrouper<INPUT> getInstance() {
        return (InputGrouper<INPUT>) INSTANCE;
    }

    private NoOpInputGrouper() {
    }

    @Override
    public Collection<Collection<Input<Object>>> grouping(Collection<Input<Object>> inputs) {
        return Collections.singletonList(inputs);
    }
}
