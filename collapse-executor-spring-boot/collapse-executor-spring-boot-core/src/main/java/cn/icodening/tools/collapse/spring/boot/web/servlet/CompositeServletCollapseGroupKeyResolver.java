package cn.icodening.tools.collapse.spring.boot.web.servlet;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collection;

/**
 * @author icodening
 * @date 2023.05.25
 */
public class CompositeServletCollapseGroupKeyResolver implements ServletCollapseGroupKeyResolver {

    private Collection<ServletCollapseGroupKeyResolver> groupKeyResolvers = new ArrayList<>();

    public CompositeServletCollapseGroupKeyResolver() {
    }

    public CompositeServletCollapseGroupKeyResolver(Collection<ServletCollapseGroupKeyResolver> groupKeyResolvers) {
        this.groupKeyResolvers = groupKeyResolvers;
    }

    public void setGroupKeyResolvers(Collection<ServletCollapseGroupKeyResolver> groupKeyResolvers) {
        this.groupKeyResolvers = groupKeyResolvers;
    }

    @Override
    public ServletCollapseGroupKey resolveGroupKey(HttpServletRequest httpServletRequest) {
        for (ServletCollapseGroupKeyResolver groupKeyResolver : groupKeyResolvers) {
            ServletCollapseGroupKey groupKey = groupKeyResolver.resolveGroupKey(httpServletRequest);
            if (groupKey != null) {
                return groupKey;
            }
        }
        return null;
    }
}
