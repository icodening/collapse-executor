package cn.icodening.collapse.sample.sequence.generator;

/**
 * ordered sequence generator
 *
 * @author icodening
 * @date 2023.07.14
 */
public interface SequenceGenerator {

    long increment(String group);

}

