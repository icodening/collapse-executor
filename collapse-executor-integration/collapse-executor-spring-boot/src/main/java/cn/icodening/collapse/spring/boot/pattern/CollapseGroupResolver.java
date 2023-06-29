package cn.icodening.collapse.spring.boot.pattern;

import org.springframework.lang.Nullable;

/**
 * @author icodening
 * @date 2023.06.29
 */
public interface CollapseGroupResolver {

    @Nullable
    RequestCollapseGroup resolve(RequestAttributes requestAttributes);
}
