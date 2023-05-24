package cn.icodening.tools.collapse.spring.boot.web.servlet;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashSet;
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
}
