package cn.icodening.collapse.spring.web.pattern;

import java.util.HashSet;
import java.util.Set;

/**
 * @author icodening
 * @date 2023.06.29
 */
public class CollapseGroupDefinition {

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
