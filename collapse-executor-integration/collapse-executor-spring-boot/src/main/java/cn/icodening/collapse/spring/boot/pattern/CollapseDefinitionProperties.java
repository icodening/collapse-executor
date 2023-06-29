package cn.icodening.collapse.spring.boot.pattern;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author icodening
 * @date 2023.06.29
 */
public class CollapseDefinitionProperties {

    private boolean enabled = false;

    private int batchSize = 32;

    private Map<String, CollapsePolicyDefinition> collapsePolicies = new LinkedHashMap<>();

    private List<CollapseGroupDefinition> collapseGroups = new ArrayList<>();

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

    public Map<String, CollapsePolicyDefinition> getCollapsePolicies() {
        return collapsePolicies;
    }

    public void setCollapsePolicies(Map<String, CollapsePolicyDefinition> collapsePolicies) {
        this.collapsePolicies = collapsePolicies;
    }

    public List<CollapseGroupDefinition> getCollapseGroups() {
        return collapseGroups;
    }

    public void setCollapseGroups(List<CollapseGroupDefinition> collapseGroups) {
        this.collapseGroups = collapseGroups;
    }
}
