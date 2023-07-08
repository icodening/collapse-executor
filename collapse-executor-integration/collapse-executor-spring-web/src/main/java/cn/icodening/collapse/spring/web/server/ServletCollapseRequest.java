package cn.icodening.collapse.spring.web.server;

import cn.icodening.collapse.spring.web.pattern.RequestCollapseGroup;

import javax.servlet.AsyncContext;
import java.util.Objects;

/**
 * @author icodening
 * @date 2023.05.22
 */
class ServletCollapseRequest {

    private final RequestCollapseGroup groupKey;

    private final AsyncContext asyncContext;

    public ServletCollapseRequest(RequestCollapseGroup groupKey, AsyncContext asyncContext) {
        this.groupKey = groupKey;
        this.asyncContext = asyncContext;
    }

    public RequestCollapseGroup getGroupKey() {
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
