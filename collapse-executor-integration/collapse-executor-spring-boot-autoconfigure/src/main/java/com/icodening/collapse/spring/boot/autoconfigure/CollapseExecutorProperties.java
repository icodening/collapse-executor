/*
 * Copyright 2023-2024 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.icodening.collapse.spring.boot.autoconfigure;

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

        private Virtual virtual = new Virtual();

        private Platform platform = new Platform();

        public Virtual getVirtual() {
            return virtual;
        }

        public void setVirtual(Virtual virtual) {
            this.virtual = virtual;
        }

        public Platform getPlatform() {
            return platform;
        }

        public void setPlatform(Platform platform) {
            this.platform = platform;
        }

        public static class Virtual {

            private boolean enabled = false;

            public boolean isEnabled() {
                return enabled;
            }

            public void setEnabled(boolean enabled) {
                this.enabled = enabled;
            }
        }

        public static class Platform {

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

}
