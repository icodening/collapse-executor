package cn.icodening.tools.collapse.spring.boot.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author icodening
 * @date 2023.05.17
 */
@ConfigurationProperties(prefix = "collapse.executor")
public class CollapseExecutorProperties {

    private boolean enabled = true;

    private long collectingWaitTime = 0;

    private int waitThreshold = 10;

    private ThreadPool threadPool = new ThreadPool();

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

    public int getWaitThreshold() {
        return waitThreshold;
    }

    public void setWaitThreshold(int waitThreshold) {
        this.waitThreshold = waitThreshold;
    }

    public ThreadPool getThreadPool() {
        return threadPool;
    }

    public void setThreadPool(ThreadPool threadPool) {
        this.threadPool = threadPool;
    }

    public static class ThreadPool {

        private int corePoolSize = 200;

        private int maximumPoolSize = corePoolSize;

        private int keepAliveTime = 0;

        private int queueSize = 0;

        private String prefix = "collapse-async";

        public int getCorePoolSize() {
            return corePoolSize;
        }

        public void setCorePoolSize(int corePoolSize) {
            this.corePoolSize = corePoolSize;
        }

        public int getMaximumPoolSize() {
            return maximumPoolSize;
        }

        public void setMaximumPoolSize(int maximumPoolSize) {
            this.maximumPoolSize = maximumPoolSize;
        }

        public int getKeepAliveTime() {
            return keepAliveTime;
        }

        public void setKeepAliveTime(int keepAliveTime) {
            this.keepAliveTime = keepAliveTime;
        }

        public int getQueueSize() {
            return queueSize;
        }

        public void setQueueSize(int queueSize) {
            this.queueSize = queueSize;
        }

        public String getPrefix() {
            return prefix;
        }

        public void setPrefix(String prefix) {
            this.prefix = prefix;
        }
    }
}
