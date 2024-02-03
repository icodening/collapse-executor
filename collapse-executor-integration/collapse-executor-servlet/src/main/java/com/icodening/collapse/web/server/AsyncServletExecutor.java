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
package com.icodening.collapse.web.server;

import com.icodening.collapse.core.Bundle;
import com.icodening.collapse.core.CollapseExecutorAsyncSupport;
import com.icodening.collapse.core.Input;
import com.icodening.collapse.core.ListeningCollector;

import javax.servlet.AsyncContext;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.ServletResponse;
import javax.servlet.ServletResponseWrapper;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeoutException;

/**
 * @author icodening
 * @date 2023.05.22
 */
public class AsyncServletExecutor extends CollapseExecutorAsyncSupport<ServletCollapseRequest, ServletCollapseResponse, CompletableFuture<ServletCollapseResponse>> {

    public AsyncServletExecutor(ListeningCollector collector) {
        super(collector);
    }

    @Override
    protected CompletableFuture<ServletCollapseResponse> doExecute(Collection<Input<ServletCollapseRequest>> inputs) {
        ServletCollapseRequest context = inputs.iterator().next().value();
        AsyncContext asyncContext = context.getAsyncContext();
        CompletableFuture<ServletCollapseResponse> collapseResponseCompletableFuture = new CompletableFuture<>();
        asyncContext.addListener(adaptAsyncListener(collapseResponseCompletableFuture));
        asyncContext.dispatch();
        return collapseResponseCompletableFuture;
    }

    @Override
    protected void bindingOutput(CompletableFuture<ServletCollapseResponse> responseCompletableFuture, List<Bundle<ServletCollapseRequest, CompletableFuture<ServletCollapseResponse>>> bundles) {
        //skip first.
        for (int i = 1; i < bundles.size(); i++) {
            Bundle<ServletCollapseRequest, CompletableFuture<ServletCollapseResponse>> bundle = bundles.get(i);
            bundle.bindOutput(responseCompletableFuture);
        }
    }

    private AsyncListener adaptAsyncListener(CompletableFuture<ServletCollapseResponse> future) {
        return new CompletableFutureAdapter(future);
    }

    private static class CompletableFutureAdapter implements AsyncListener {

        private final CompletableFuture<ServletCollapseResponse> future;

        private CompletableFutureAdapter(CompletableFuture<ServletCollapseResponse> future) {
            this.future = future;
        }

        @Override
        public void onComplete(AsyncEvent event) throws IOException {
            ServletResponse suppliedResponse = event.getSuppliedResponse();
            RecordableServletResponse repeatableReadServletResponse = findRecordableServletResponse(suppliedResponse);
            if (repeatableReadServletResponse == null) {
                onError(new AsyncEvent(event.getAsyncContext(), new IllegalArgumentException("RecordableServletResponse not found.")));
                return;
            }
            RecordableServletOutputStream outputStream = repeatableReadServletResponse.getOutputStream();
            ServletCollapseResponse servletCollapseResponse = new ServletCollapseResponse(repeatableReadServletResponse, outputStream);
            future.complete(servletCollapseResponse);
        }

        @Override
        public void onTimeout(AsyncEvent event) throws IOException {
            future.completeExceptionally(new TimeoutException());
        }

        @Override
        public void onError(AsyncEvent event) throws IOException {
            future.completeExceptionally(event.getThrowable());
        }

        @Override
        public void onStartAsync(AsyncEvent event) throws IOException {

        }

        private static RecordableServletResponse findRecordableServletResponse(ServletResponse servletResponse) {
            ServletResponse response = servletResponse;
            while (response instanceof ServletResponseWrapper) {
                if (response instanceof RecordableServletResponse) {
                    return (RecordableServletResponse) response;
                }
                response = ((ServletResponseWrapper) servletResponse).getResponse();
            }
            return null;
        }
    }
}
