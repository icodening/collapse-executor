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
package com.icodening.collapse.sample.advanced.support;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author icodening
 * @date 2023.06.27
 */
public class UserService {

    private final Map<Long, UserEntity> users = new ConcurrentHashMap<>();

    public UserService() {
        initialize();
    }

    public void initialize() {
        for (long i = 1; i <= 10; i++) {
            users.put(i, new UserEntity(i, UUID.randomUUID().toString()));
        }
    }

    public UserEntity getOne(Long id) {
        if (id == null) {
            return null;
        }
        return users.get(id);
    }

    public List<UserEntity> query(List<Long> ids) {
        if (ids == null) {
            return Collections.emptyList();
        }
        return ids.stream().map(users::get).collect(Collectors.toList());
    }
}
