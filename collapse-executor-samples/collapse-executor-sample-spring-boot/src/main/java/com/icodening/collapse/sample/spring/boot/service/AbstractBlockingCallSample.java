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

import com.icodening.collapse.sample.spring.boot.controller.UserController;
import com.icodening.collapse.sample.spring.boot.entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.concurrent.ExecutorService;

/**
 * @author icodening
 * @date 2023.06.24
 */
public abstract class AbstractBlockingCallSample {

    private final String prefix;

    protected ExecutorService executorService;

    private int serverPort;

    protected AbstractBlockingCallSample(String prefix) {
        this.prefix = prefix;
    }

    @Autowired
    public void setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
    }

    @Value("${server.port}")
    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void processOnStarted() {
        UriComponentsBuilder baseUriBuilder = UriComponentsBuilder.fromUriString("http://localhost").port(serverPort);
        try {
            System.out.println("--------------------------------[" + prefix + "] start----------------------------------");
            queryId1between50(executorService, baseUriBuilder);
            queryId1between50Batch();
            queryId1between2(executorService, baseUriBuilder);
            System.out.println("--------------------------------[" + prefix + "] end------------------------------------\n\n\n");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * query id [1,50] in 50 times
     */
    protected void queryId1between50(ExecutorService executorService, UriComponentsBuilder baseUri) throws InterruptedException {
        System.out.println("----------------------------------query id [1,50]----------------------------------");
        for (int i = 1; i <= 50; i++) {
            final long id = i;
            executorService.submit(() -> {
                try {
                    UserEntity userEntity = doSingleQuery(baseUri.cloneBuilder(), id);
                    System.out.println("query id:" + id + ", " + userEntity);
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            });
        }
        Thread.sleep(500);
        System.out.println(prefix + " /user/{id} execute times:" + UserController.SINGLE_GET_COUNTER.intValue());//expected result equals 50
        UserController.SINGLE_GET_COUNTER.set(0);
    }

    protected void queryId1between50Batch() throws InterruptedException {
        System.out.println("----------------------------------batch query id [1,50]----------------------------------");
        for (int i = 1; i <= 50; i++) {
            final long id = i;
            executorService.submit(() -> {
                try {
                    UserEntity userEntity = doBatchQuery(id);
                    System.out.println("query id:" + id + ", " + userEntity);
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            });
        }
        Thread.sleep(500);
        System.out.println(prefix + " /user/batchGet execute times:" + UserController.BATCH_GET_COUNTER.intValue());//expected result less than 50
        UserController.BATCH_GET_COUNTER.set(0);
    }


    /**
     * query id [1,2] in 50 times
     */
    protected void queryId1between2(ExecutorService executorService, UriComponentsBuilder baseUri) throws InterruptedException {
        System.out.println("----------------------------------query id [1,2]----------------------------------");
        for (int i = 1; i <= 50; i++) {
            final long id = i % 2 + 1;
            executorService.submit(() -> {
                try {
                    UserEntity userEntity = doSingleQuery(baseUri.cloneBuilder(), id);
                    System.out.println("query id:" + id + ", " + userEntity);
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            });
        }
        Thread.sleep(500);
        System.out.println(prefix + " /user/{id} execute times:" + UserController.SINGLE_GET_COUNTER.intValue());//expected result less than 50
        UserController.SINGLE_GET_COUNTER.set(0);
    }

    protected abstract UserEntity doBatchQuery(long id) throws Throwable;

    protected abstract UserEntity doSingleQuery(UriComponentsBuilder baseUri, long id) throws Throwable;
}
