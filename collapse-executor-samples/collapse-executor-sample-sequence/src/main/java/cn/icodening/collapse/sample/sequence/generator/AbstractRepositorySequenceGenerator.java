package cn.icodening.collapse.sample.sequence.generator;

import cn.icodening.collapse.core.Bundle;
import cn.icodening.collapse.core.CollapseExecutorBlockingSupport;
import cn.icodening.collapse.core.EqualsInputGrouper;
import cn.icodening.collapse.core.Input;
import cn.icodening.collapse.core.InputGrouper;
import cn.icodening.collapse.core.LengthLimitedInputGrouper;

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
