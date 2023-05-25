package cn.icodening.tools.collapse.spring.boot.web.servlet;

import java.util.Objects;
import java.util.TreeMap;

/**
 * @author icodening
 * @date 2023.05.25
 */
public class ServletCollapseGroupKey {

    private String method;

    private String path;

    private String query;

    private TreeMap<String, String> headers = new TreeMap<>(String::compareTo);

    private byte[] body;

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public TreeMap<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(TreeMap<String, String> headers) {
        this.headers = headers;
    }

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServletCollapseGroupKey that = (ServletCollapseGroupKey) o;
        return Objects.equals(method, that.method) && Objects.equals(path, that.path) && Objects.equals(query, that.query) && Objects.equals(headers, that.headers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(method, path, query, headers);
    }
}
