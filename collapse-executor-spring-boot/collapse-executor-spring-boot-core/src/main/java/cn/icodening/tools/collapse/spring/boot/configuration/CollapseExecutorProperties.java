package cn.icodening.tools.collapse.spring.boot.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author icodening
 * @date 2023.05.17
 */
@ConfigurationProperties(prefix = "collapse.executor")
public class CollapseExecutorProperties {

    private boolean enabled = true;

    private long collectingWaitTime = -1;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public long getCollectingWaitTime() {
        return collectingWaitTime;
    }

    public void setCollectingWaitTime(long collectingWaitTime) {
        this.collectingWaitTime = collectingWaitTime;
    }
}
