package cn.icodening.collapse.core;

import java.util.Collection;

/**
 * input sharding tool
 *
 * @author icodening
 * @date 2023.05.14
 */
@FunctionalInterface
public interface InputGrouper<INPUT> {

    Collection<Collection<Input<INPUT>>> grouping(Collection<Input<INPUT>> inputs);

}
