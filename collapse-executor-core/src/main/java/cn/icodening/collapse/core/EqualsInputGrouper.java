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
