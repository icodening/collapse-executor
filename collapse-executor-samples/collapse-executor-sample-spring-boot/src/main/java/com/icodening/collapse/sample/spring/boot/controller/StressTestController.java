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
package com.icodening.collapse.sample.spring.boot.controller;

import com.icodening.collapse.sample.spring.boot.entity.UserEntity;
import com.icodening.collapse.sample.spring.boot.service.UserService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author icodening
 * @date 2023.05.24
 */
@RequestMapping("/test")
@RestController
public class StressTestController {

    private final AtomicInteger collapseCounter = new AtomicInteger(0);

    private final AtomicInteger noOpCounter = new AtomicInteger(0);

    private final UserService userService;

    public StressTestController(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping("/collapse0")
    public UserEntity collapse0() {
        collapseCounter.incrementAndGet();
        return userService.getUser(1L);
    }

    @RequestMapping("/collapse1")
    public UserEntity collapse1() throws InterruptedException {
        collapseCounter.incrementAndGet();
        Thread.sleep(1);
        return userService.getUser(1L);
    }

    @RequestMapping("/collapse100")
    public UserEntity collapse100() throws InterruptedException {
        collapseCounter.incrementAndGet();
        Thread.sleep(100);
        return userService.getUser(1L);
    }

    @RequestMapping("/noop0")
    public UserEntity noop0() {
        noOpCounter.incrementAndGet();
        return userService.getUser(1L);
    }

    @RequestMapping("/noop1")
    public UserEntity noop1() throws InterruptedException {
        noOpCounter.incrementAndGet();
        Thread.sleep(1);
        return userService.getUser(1L);
    }

    @RequestMapping("/noop100")
    public UserEntity noop100() throws InterruptedException {
        noOpCounter.incrementAndGet();
        Thread.sleep(100);
        return userService.getUser(1L);
    }

    @RequestMapping("/counter")
    public Map<String, Integer> counter() {
        Map<String, Integer> result = new HashMap<>();
        result.put("collapse", collapseCounter.get());
        result.put("noop", noOpCounter.get());
        return result;
    }

    @RequestMapping("/reset")
    public String reset() {
        collapseCounter.set(0);
        noOpCounter.set(0);
        return "reset success.";
    }
}
