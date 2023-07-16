package cn.icodening.collapse.web.pattern;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author icodening
 * @date 2023.06.29
 */
public class CollapseGroupDefinition {

    private String collapsePolicyName;

    private Set<String> patterns = new LinkedHashSet<>();

    public String getCollapsePolicyName() {
        return collapsePolicyName;
    }

    public void setCollapsePolicyName(String collapsePolicyName) {
        this.collapsePolicyName = collapsePolicyName;
    }

    public Set<String> getPatterns() {
        return patterns;
    }

    public void setPatterns(Set<String> patterns) {
        this.patterns = patterns;
    }
}
