package cn.icodening.tools.collapse.spring.boot.web.servlet;

import javax.servlet.AsyncContext;
import java.util.Objects;

/**
 * @author icodening
 * @date 2023.05.22
 */
class ServletCollapseRequest {

    private final ServletCollapseGroupKey groupKey;

    private final AsyncContext asyncContext;

    public ServletCollapseRequest(ServletCollapseGroupKey groupKey, AsyncContext asyncContext) {
        this.groupKey = groupKey;
        this.asyncContext = asyncContext;
    }

    public ServletCollapseGroupKey getGroupKey() {
        return groupKey;
    }

    public AsyncContext getAsyncContext() {
        return asyncContext;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServletCollapseRequest that = (ServletCollapseRequest) o;
        return groupKey.equals(that.groupKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(groupKey);
    }
}
