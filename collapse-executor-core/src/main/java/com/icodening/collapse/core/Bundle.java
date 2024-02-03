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
package com.icodening.collapse.core;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * input output bundle
 *
 * @author icodening
 * @date 2023.05.14
 */
public final class Bundle<INPUT, OUTPUT> {

    private final CollapseExecutor<INPUT, OUTPUT> collapseExecutor;

    private final INPUT input;

    private volatile OUTPUT output;

    private volatile Throwable throwable;

    private final Executor callbackExecutor;

    private final CompletableFuture<OUTPUT> listeningResult;

    private volatile boolean completed = false;

    Bundle(CollapseExecutor<INPUT, OUTPUT> collapseExecutor, INPUT input, Executor callbackExecutor, CompletableFuture<OUTPUT> listeningResult) {
        this.collapseExecutor = collapseExecutor;
        this.input = input;
        this.callbackExecutor = callbackExecutor;
        this.listeningResult = listeningResult;
    }

    CollapseExecutor<INPUT, OUTPUT> getCollapseExecutor() {
        return collapseExecutor;
    }

    public INPUT getInput() {
        return input;
    }

    public OUTPUT getOutput() {
        return output;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void bindOutput(OUTPUT output) {
        this.bindOutput(output, null);
    }

    public void bindOutput(OUTPUT output, Throwable throwable) {
        if (completed) {
            return;
        }
        completed = true;
        if (throwable != null) {
            this.throwable = throwable;
            callbackExecutor.execute(() -> listeningResult.completeExceptionally(throwable));
        } else {
            this.output = output;
            callbackExecutor.execute(() -> listeningResult.complete(output));
        }
    }

    Executor getCallbackExecutor() {
        return callbackExecutor;
    }

    CompletableFuture<OUTPUT> getListeningResult() {
        return listeningResult;
    }
}