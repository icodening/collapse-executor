package cn.icodening.collapse.web.pattern;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

/**
 * @author icodening
 * @date 2023.06.29
 */
public class RequestCollapseGroup {

    private String identifier;

    private String method;

    private String path;

    private Map<String, List<String>> headers = new TreeMap<>(String::compareToIgnoreCase);

    private Map<String, List<String>> queries = new HashMap<>();

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

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

    public Map<String, List<String>> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, List<String>> headers) {
        this.headers = headers;
    }

    public Map<String, List<String>> getQueries() {
        return queries;
    }

    public void setQueries(Map<String, List<String>> queries) {
        this.queries = queries;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RequestCollapseGroup that = (RequestCollapseGroup) o;
        return Objects.equals(identifier, that.identifier) && Objects.equals(method, that.method) && Objects.equals(path, that.path) && Objects.equals(headers, that.headers) && Objects.equals(queries, that.queries);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier, method, path, headers, queries);
    }
}
