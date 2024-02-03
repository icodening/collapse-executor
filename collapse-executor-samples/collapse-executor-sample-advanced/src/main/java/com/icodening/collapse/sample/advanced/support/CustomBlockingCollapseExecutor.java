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

import com.icodening.collapse.core.Bundle;
import com.icodening.collapse.core.CollapseExecutorBlockingSupport;
import com.icodening.collapse.core.Input;
import com.icodening.collapse.core.LengthLimitedInputGrouper;
import com.icodening.collapse.core.ListeningCollector;
import com.icodening.collapse.core.NoOpInputGrouper;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author icodening
 * @date 2023.06.27
 */
public class CustomBlockingCollapseExecutor extends CollapseExecutorBlockingSupport<Long, UserEntity, Map<Long, UserEntity>> {

    /**
     * mock remote service
     */
    private final UserService userService;

    public CustomBlockingCollapseExecutor(ListeningCollector collector, UserService userService) {
        super(collector);
        this.userService = userService;
        this.setBatchSize(Integer.MAX_VALUE);
    }

    public void setBatchSize(int size) {
        this.setInputGrouper(LengthLimitedInputGrouper.newInstance(size, NoOpInputGrouper.getInstance()));
    }

    @Override
    protected Map<Long, UserEntity> doExecute(Collection<Input<Long>> inputs) {
        //merge inputs and batch fetch
        List<Long> ids = inputs.stream().map(Input::value).collect(Collectors.toList());
        List<UserEntity> userList = userService.query(ids);
        System.out.println("[" + Thread.currentThread().getName() + "] batch query ids:" + ids + "\n--------------------------------------------------------------------------------------------------");
        Map<Long, UserEntity> idUsers = new HashMap<>();
        for (UserEntity userEntity : userList) {
            if (userEntity == null) {
                continue;
            }
            Long id = userEntity.getId();
            idUsers.put(id, userEntity);
        }
        return idUsers;
    }

    @Override
    protected void bindingOutput(Map<Long, UserEntity> users, List<Bundle<Long, UserEntity>> bundles) {
        for (Bundle<Long, UserEntity> bundle : bundles) {
            Long inputId = bundle.getInput();
            UserEntity userEntity = users.get(inputId);
            if (userEntity == null) {
                bundle.bindOutput(null);
                continue;
            }
            //copy a response
            UserEntity duplicate = new UserEntity();
            duplicate.setId(userEntity.getId());
            duplicate.setUsername(userEntity.getUsername());
            bundle.bindOutput(duplicate);
        }
    }
}
