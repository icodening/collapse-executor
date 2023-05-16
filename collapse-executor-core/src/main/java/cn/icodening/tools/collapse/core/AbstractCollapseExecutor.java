package cn.icodening.tools.collapse.core;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

/**
 * @author icodening
 * @date 2023.05.14
 */
public abstract class AbstractCollapseExecutor<INPUT, OUTPUT, BATCH_OUTPUT> implements CollapseExecutor<INPUT, OUTPUT> {

    private final ListeningBundleCollector collector;

    private InputGrouper<INPUT> inputGrouper = EqualsInputGrouper.getInstance();

    public AbstractCollapseExecutor(Executor executor) {
        this(new ListeningBundleCollector(executor));
    }

    public AbstractCollapseExecutor(ListeningBundleCollector collector) {
        this.collector = Objects.requireNonNull(collector, "collector must be not null.");
        this.collector.addListener(this, this::collapseExecute);
    }

    public void setInputGrouper(InputGrouper<INPUT> inputGrouper) {
        this.inputGrouper = inputGrouper;
    }

    public InputGrouper<INPUT> getInputGrouper() {
        return inputGrouper;
    }

    protected ListeningBundleCollector getCollector() {
        return collector;
    }

    @Override
    public OUTPUT execute(INPUT input) throws Throwable {
        ThreadlessExecutor threadlessExecutor = new ThreadlessExecutor();
        CompletableFuture<OUTPUT> completableFuture = new CompletableFuture<>();
        this.collector.enqueue(createBundle(input, threadlessExecutor, completableFuture));
        try {
            while (!completableFuture.isDone()) {
                threadlessExecutor.waitAndDrain();
            }
            return completableFuture.get();
        } catch (ExecutionException executionException) {
            //throw actual exception
            throw executionException.getCause();
        } finally {
            threadlessExecutor.shutdown();
        }
    }

    protected abstract BATCH_OUTPUT doExecute(Collection<Input<INPUT>> inputs) throws Throwable;

    protected abstract void bindingOutput(BATCH_OUTPUT compositeOut, List<Bundle<INPUT, OUTPUT>> bundles);

    @SuppressWarnings("unchecked")
    private Bundle<Object, Object> createBundle(INPUT input, ThreadlessExecutor executor, CompletableFuture<OUTPUT> completableFuture) {
        return (Bundle<Object, Object>) new Bundle<>(this, input, executor, completableFuture);
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
            input.getBundle().getThreadlessExecutor().execute(() -> {
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
    public int hashCode() {
        return super.hashCode();
    }
}
