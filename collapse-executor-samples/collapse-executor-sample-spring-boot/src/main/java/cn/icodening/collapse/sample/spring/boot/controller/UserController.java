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
package cn.icodening.collapse.sample.spring.boot.controller;

import cn.icodening.collapse.sample.spring.boot.entity.UserEntity;
import cn.icodening.collapse.sample.spring.boot.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author icodening
 * @date 2023.05.17
 */
@RestController
@RequestMapping("/user")
public class UserController {

    public static final AtomicInteger SINGLE_GET_COUNTER = new AtomicInteger(0);

    public static final AtomicInteger BATCH_GET_COUNTER = new AtomicInteger(0);

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public UserEntity singleGetUser(@PathVariable Long id) {
        SINGLE_GET_COUNTER.incrementAndGet();
        return userService.getUser(id);
    }

    @GetMapping("/batchGet")
    public List<UserEntity> batchGetUser(@RequestParam(name = "id") Long[] ids) {
        BATCH_GET_COUNTER.incrementAndGet();
        return userService.batchGet(ids);
    }

}
