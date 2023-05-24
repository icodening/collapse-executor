package cn.icodening.tools.collapse.spring.boot.web.servlet;

import javax.servlet.AsyncContext;
import java.util.Objects;

/**
 * @author icodening
 * @date 2023.05.22
 */
class ServletCollapseRequest {

    private final String uri;

    private final AsyncContext asyncContext;

    public ServletCollapseRequest(String uri, AsyncContext asyncContext) {
        this.uri = uri;
        this.asyncContext = asyncContext;
    }

    public String getUri() {
        return uri;
    }

    public AsyncContext getAsyncContext() {
        return asyncContext;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServletCollapseRequest that = (ServletCollapseRequest) o;
        return uri.equals(that.uri);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uri);
    }
}
