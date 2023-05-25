package cn.icodening.tools.collapse.spring.boot.web.servlet;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author icodening
 * @date 2023.05.23
 */
@ConfigurationProperties(prefix = "collapse.executor.servlet")
public class CollapseServletProperties {

    private boolean enabled = false;

    private int batchSize = 32;

    private Set<String> uris = new HashSet<>();

    private Map<String, CollapsePolicy> collapsePolicies = new LinkedHashMap<>();

    private List<CollapseGroup> collapseGroups = new ArrayList<>();

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    public Set<String> getUris() {
        return uris;
    }

    public void setUris(Set<String> uris) {
        this.uris = uris;
    }

    public Map<String, CollapsePolicy> getCollapsePolicies() {
        return collapsePolicies;
    }

    public void setCollapsePolicies(Map<String, CollapsePolicy> collapsePolicies) {
        this.collapsePolicies = collapsePolicies;
    }

    public List<CollapseGroup> getCollapseGroups() {
        return collapseGroups;
    }

    public void setCollapseGroups(List<CollapseGroup> collapseCollapseGroups) {
        this.collapseGroups = collapseCollapseGroups;
    }

    public static class CollapsePolicy {

        private boolean collapseQueryString = true;

        private Set<String> collapseRequestHeaders = new HashSet<>();

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
    }

    public static class CollapseGroup {

        private String collapsePolicyName;

        private Set<String> uris = new HashSet<>();

        public String getCollapsePolicyName() {
            return collapsePolicyName;
        }

        public void setCollapsePolicyName(String collapsePolicyName) {
            this.collapsePolicyName = collapsePolicyName;
        }

        public Set<String> getUris() {
            return uris;
        }

        public void setUris(Set<String> uris) {
            this.uris = uris;
        }
    }
}
