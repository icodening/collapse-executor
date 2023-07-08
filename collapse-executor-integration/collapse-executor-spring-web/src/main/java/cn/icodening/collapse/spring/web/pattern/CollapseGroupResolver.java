package cn.icodening.collapse.spring.web.pattern;

/**
 * @author icodening
 * @date 2023.06.29
 */
public interface CollapseGroupResolver {

    /**
     * resolve collapse group from request attributes
     * @param requestAttributes request attributes, include http url、http method、http header
     * @return null or collapse group, return null when not resolved any collapse group.
     */
    RequestCollapseGroup resolve(RequestAttributes requestAttributes);
}
