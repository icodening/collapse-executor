package cn.icodening.collapse.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * @author icodening
 * @date 2023.05.14
 */
public class EqualsInputGrouper implements InputGrouper<Object> {

    private static final InputGrouper<?> INSTANCE = new EqualsInputGrouper();

    @SuppressWarnings("unchecked")
    public static <INPUT> InputGrouper<INPUT> getInstance() {
        return (InputGrouper<INPUT>) INSTANCE;
    }

    private EqualsInputGrouper() {
    }

    @Override
    public Collection<Collection<Input<Object>>> grouping(Collection<Input<Object>> inputs) {
        return inputs.stream()
                .collect(Collectors.groupingBy(
                        (Input::value),
                        Collectors.toCollection((Supplier<Collection<Input<Object>>>) ArrayList::new)
                ))
                .values();
    }
}
