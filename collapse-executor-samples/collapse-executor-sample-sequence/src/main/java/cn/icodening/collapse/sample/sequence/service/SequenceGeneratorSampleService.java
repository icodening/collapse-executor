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
package cn.icodening.collapse.sample.sequence.service;

import cn.icodening.collapse.sample.sequence.generator.SequenceGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author icodening
 * @date 2023.07.14
 */
@Service
public class SequenceGeneratorSampleService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SequenceGeneratorSampleService.class);

    private final SequenceGenerator sequenceGenerator;

    private final ExecutorService executorService;

    public SequenceGeneratorSampleService(SequenceGenerator sequenceGenerator) {
        this.sequenceGenerator = sequenceGenerator;
        CustomizableThreadFactory threadFactory = new CustomizableThreadFactory("sequence-generator");
        threadFactory.setDaemon(true);
        this.executorService = new ThreadPoolExecutor(10, 10,
                0, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(),
                threadFactory);
    }

    public void concurrentIncrement(final String businessType) {
        for (int i = 0; i < 10; i++) {
            this.executorService.execute(() -> {
                try {
                    long sequence = this.sequenceGenerator.increment(businessType);
                    LOGGER.info("current sequence: {}", sequence);
                } catch (IllegalArgumentException e) {
                    LOGGER.error(e.getMessage());
                }
            });
        }
    }
}
