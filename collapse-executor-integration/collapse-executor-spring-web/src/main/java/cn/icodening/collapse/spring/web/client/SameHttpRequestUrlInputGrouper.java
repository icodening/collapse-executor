package cn.icodening.collapse.spring.web.client;

import cn.icodening.collapse.core.Input;
import cn.icodening.collapse.core.InputGrouper;
import org.springframework.http.RequestEntity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * @author icodening
 * @date 2023.05.19
 */
public class SameHttpRequestUrlInputGrouper implements InputGrouper<RequestEntity<?>> {

    private static final InputGrouper<?> INSTANCE = new SameHttpRequestUrlInputGrouper();

    @SuppressWarnings("unchecked")
    public static <INPUT> InputGrouper<INPUT> getInstance() {
        return (InputGrouper<INPUT>) INSTANCE;
    }

    @Override
    public Collection<Collection<Input<RequestEntity<?>>>> grouping(Collection<Input<RequestEntity<?>>> inputs) {
        return inputs.stream()
                .collect(Collectors.groupingBy(
                        //eg. GET http://localhost:8080/users/1
                        (input) -> input.value().getMethod() + " " + input.value().getUrl(),
                        Collectors.toCollection((Supplier<Collection<Input<RequestEntity<?>>>>) ArrayList::new)
                ))
                .values();
    }
}
