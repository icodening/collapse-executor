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
package com.icodening.collapse.sample.sequence.generator;

import com.icodening.collapse.core.Bundle;
import com.icodening.collapse.core.CollapseExecutorBlockingSupport;
import com.icodening.collapse.core.EqualsInputGrouper;
import com.icodening.collapse.core.Input;
import com.icodening.collapse.core.InputGrouper;
import com.icodening.collapse.core.LengthLimitedInputGrouper;

import java.util.Collection;
import java.util.List;

/**
 * @author icodening
 * @date 2023.07.14
 */
public abstract class AbstractRepositorySequenceGenerator extends CollapseExecutorBlockingSupport<String, Long, Long> implements SequenceGenerator {

    public AbstractRepositorySequenceGenerator() {
        super();
    }

    @Override
    public final void setInputGrouper(InputGrouper<String> inputGrouper) {
        if (!((InputGrouper<?>) inputGrouper instanceof LengthLimitedInputGrouper)) {
            return;
        }
        //only support EqualsInputGrouper
        InputGrouper<Object> delegate = ((LengthLimitedInputGrouper) (InputGrouper<?>) inputGrouper).getDelegate();
        if (!(delegate instanceof EqualsInputGrouper)) {
            return;
        }
        super.setInputGrouper(inputGrouper);
    }

    @Override
    public final long increment(String group) {
        try {
            return execute(group);
        } catch (RuntimeException e) {
            throw e;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected final Long doExecute(Collection<Input<String>> inputs) {
        String group = inputs.iterator().next().value();
        int incrementBy = inputs.size();
        return increment(group, incrementBy);
    }

    @Override
    protected final void bindingOutput(Long result, List<Bundle<String, Long>> bundles) {
        //map sequence for each thread
        for (int idx = 0, decrement = bundles.size() - 1; idx < bundles.size(); idx++, decrement--) {
            long incr = result - decrement;
            Bundle<String, Long> bundle = bundles.get(idx);
            bundle.bindOutput(incr);
        }
    }

    protected abstract Long increment(String group, int incrementBy);

}
