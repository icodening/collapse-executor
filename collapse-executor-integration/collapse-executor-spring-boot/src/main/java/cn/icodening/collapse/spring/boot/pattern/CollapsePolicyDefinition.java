package cn.icodening.collapse.spring.boot.pattern;

import java.util.HashSet;
import java.util.Set;

/**
 * @author icodening
 * @date 2023.06.29
 */
public class CollapsePolicyDefinition {

    private boolean collapseQueryString = true;

    private Set<String> collapseRequestHeaders = new HashSet<>();

    private Set<String> collapseRequestQueries = new HashSet<>();

    public boolean isCollapseQueryString() {
        return collapseQueryString;
    }

    public void setCollapseQueryString(boolean collapseQueryString) {
        this.collapseQueryString = collapseQueryString;
    }

    public Set<String> getCollapseRequestHeaders() {
        return collapseRequestHeaders;
    }

    public void setCollapseRequestHeaders(Set<String> collapseRequestHeaders) {
        this.collapseRequestHeaders = collapseRequestHeaders;
    }

    public Set<String> getCollapseRequestQueries() {
        return collapseRequestQueries;
    }

    public void setCollapseRequestQueries(Set<String> collapseRequestQueries) {
        this.collapseRequestQueries = collapseRequestQueries;
    }
}
