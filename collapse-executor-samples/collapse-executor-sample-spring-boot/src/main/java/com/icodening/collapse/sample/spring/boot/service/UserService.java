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
package com.icodening.collapse.sample.spring.boot.service;

import com.icodening.collapse.sample.spring.boot.entity.UserEntity;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author icodening
 * @date 2023.05.17
 */
@Service
public class UserService {

    private final Map<Long, UserEntity> idUserMap = new ConcurrentHashMap<>();

    @PostConstruct
    public void initUsers() {
        for (long i = 1; i <= 50; i++) {
            UserEntity userEntity = new UserEntity();
            userEntity.setId(i);
            userEntity.setUsername("User " + i);
            userEntity.setPassword(UUID.randomUUID().toString());
            idUserMap.put(i, userEntity);
        }
    }

    public UserEntity getUser(Long id) {
        return idUserMap.get(id);
    }

    public List<UserEntity> batchGet(Long[] ids) {
        return Arrays.stream(ids).map(idUserMap::get).collect(Collectors.toList());
    }
}
