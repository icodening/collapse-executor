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
package com.icodening.collapse.sample.sequence;

import com.icodening.collapse.sample.sequence.service.SequenceGeneratorSampleService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author icodening
 * @date 2023.07.14
 */
@SpringBootApplication
public class SequenceApplication {

    public static void main(String[] args) throws InterruptedException {
        ConfigurableApplicationContext context = SpringApplication.run(SequenceApplication.class);
        SequenceGeneratorSampleService sampleService = context.getBean(SequenceGeneratorSampleService.class);
        System.out.println("\n========================= get 'order' sequence===========================");
        sampleService.concurrentIncrement("order");

        Thread.sleep(300);
        System.out.println("\n========================= get 'product' sequence===========================");
        sampleService.concurrentIncrement("product");

        Thread.sleep(300);
        System.out.println("\n========================= get 'member' sequence [not exists]===========================");
        sampleService.concurrentIncrement("member");
    }
}
