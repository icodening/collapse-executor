package cn.icodening.collapse.core;

/**
 * indicator a CollapseExecutor's name
 *
 * @author icodening
 * @date 2023.07.06
 */
public interface NamedCollapseExecutor {

    /**
     * get name of the {@link CollapseExecutor}
     *
     * @return collapse executor's name
     */
    default String getName() {
        return this.getClass().getSimpleName();
    }
}
