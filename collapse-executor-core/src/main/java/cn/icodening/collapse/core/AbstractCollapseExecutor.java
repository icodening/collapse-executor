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

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

/**
 * @author icodening
 * @date 2023.05.14
 */
public abstract class AbstractCollapseExecutor<INPUT, OUTPUT, BATCH_OUTPUT> implements CollapseExecutor<INPUT, OUTPUT>, NamedCollapseExecutor {

    private ListeningCollector collector;

    private String name = this.getClass().getSimpleName();

    private InputGrouper<INPUT> inputGrouper = EqualsInputGrouper.getInstance();

    public AbstractCollapseExecutor() {
        this(new SuspendableCollector());
    }

    public AbstractCollapseExecutor(ListeningCollector collector) {
        this.collector = Objects.requireNonNull(collector, "collector must be not null.");
        this.setCollector(collector);
    }

    public void setCollector(ListeningCollector collector) {
        ListeningCollector old = this.collector;
        if (old != null) {
            old.removeListener(this);
        }
        this.collector = collector;
        this.collector.addListener(this, this::collapseExecute);
    }

    public void setInputGrouper(InputGrouper<INPUT> inputGrouper) {
        this.inputGrouper = Objects.requireNonNull(inputGrouper, "inputGrouper must be not null.");
    }

    public InputGrouper<INPUT> getInputGrouper() {
        return inputGrouper;
    }

    protected ListeningCollector getCollector() {
        return collector;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    @SuppressWarnings("unchecked")
    public OUTPUT execute(INPUT input) throws Throwable {
        Executor callbackExecutor = getCallbackExecutor();
        Bundle<INPUT, OUTPUT> bundle = createBundle(input, callbackExecutor, new CompletableFuture<>());
        ListeningCollector collector = Objects.requireNonNull(this.collector, "collector must be not null.");
        collector.enqueue((Bundle<Object, Object>) bundle);
        return returning(bundle);
    }

    protected abstract OUTPUT returning(Bundle<INPUT, OUTPUT> bundle) throws Throwable;

    protected abstract Executor getCallbackExecutor();

    protected abstract BATCH_OUTPUT doExecute(Collection<Input<INPUT>> inputs) throws Throwable;

    protected abstract void bindingOutput(BATCH_OUTPUT batchOutput, List<Bundle<INPUT, OUTPUT>> bundles);

    private Bundle<INPUT, OUTPUT> createBundle(INPUT input, Executor executor, CompletableFuture<OUTPUT> completableFuture) {
        return new Bundle<>(this, input, executor, completableFuture);
    }

    @SuppressWarnings("unchecked")
    private void collapseExecute(Collection<Bundle<Object, Object>> bundles) {
        List<Input<INPUT>> inputsSnapshot = bundles.stream().map(bundle -> (Input<INPUT>) new Input<>(bundle.getInput(), bundle)).collect(Collectors.toList());
        Collection<Collection<Input<INPUT>>> groups = inputGrouper.grouping(inputsSnapshot);
        for (Collection<Input<INPUT>> group : groups) {
            if (group == null) {
                continue;
            }
            Iterator<Input<INPUT>> iterator = group.iterator();
            if (!iterator.hasNext()) {
                continue;
            }
            List<Bundle<INPUT, OUTPUT>> bundleGroup = group.stream().map(input -> (Bundle<INPUT, OUTPUT>) input.getBundle()).collect(Collectors.toList());
            Input<INPUT> input = iterator.next();
            input.getBundle().getCallbackExecutor().execute(() -> {
                try {
                    BATCH_OUTPUT batchOutput = this.doExecute(group);
                    this.bindingOutput(batchOutput, bundleGroup);
                } catch (Throwable e) {
                    for (Bundle<?, ?> bundle : bundleGroup) {
                        bundle.bindOutput(null, e);
                    }
                }
            });
        }
    }

    @Override
    public final boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public final int hashCode() {
        return super.hashCode();
    }
}
