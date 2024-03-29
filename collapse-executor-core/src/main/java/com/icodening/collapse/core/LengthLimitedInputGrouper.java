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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * @author icodening
 * @date 2023.05.14
 */
public class LengthLimitedInputGrouper implements InputGrouper<Object> {

    private static final int DEFAULT_CHUCK_SIZE = 16;

    private final int chuckSize;

    private final InputGrouper<Object> delegate;

    public static <INPUT> InputGrouper<INPUT> newInstance(InputGrouper<INPUT> delegate) {
        return newInstance(DEFAULT_CHUCK_SIZE, delegate);
    }

    @SuppressWarnings("unchecked")
    public static <INPUT> InputGrouper<INPUT> newInstance(int chuckSize, InputGrouper<INPUT> delegate) {
        return (InputGrouper<INPUT>) new LengthLimitedInputGrouper(chuckSize, (InputGrouper<Object>) delegate);
    }

    private LengthLimitedInputGrouper(InputGrouper<Object> delegate) {
        this(DEFAULT_CHUCK_SIZE, delegate);
    }

    private LengthLimitedInputGrouper(int chuckSize, InputGrouper<Object> delegate) {
        this.chuckSize = chuckSize;
        this.delegate = Objects.requireNonNull(delegate, "delegate must be not null.");
    }

    @SuppressWarnings("unchecked")
    public <INPUT> InputGrouper<INPUT> getDelegate() {
        return (InputGrouper<INPUT>) delegate;
    }

    @Override
    public Collection<Collection<Input<Object>>> grouping(Collection<Input<Object>> inputs) {
        Collection<Collection<Input<Object>>> groups = delegate.grouping(inputs);
        List<Collection<Input<Object>>> result = new ArrayList<>(groups.size());
        for (Collection<Input<Object>> group : groups) {
            Iterator<Input<Object>> iterator = group.iterator();
            int remaining = group.size();
            do {
                List<Input<Object>> tmp = new ArrayList<>(Math.min(chuckSize, remaining));
                for (int i = 0; i < chuckSize && iterator.hasNext(); i++) {
                    tmp.add(iterator.next());
                }
                result.add(tmp);
                remaining -= chuckSize;
            } while (iterator.hasNext());
        }
        return result;
    }
}
