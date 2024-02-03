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
package com.icodening.collapse.spring.web.client.reactive;

import com.icodening.collapse.core.support.FutureCallableGroupCollapseExecutor;
import com.icodening.collapse.web.pattern.CollapseGroupResolver;
import com.icodening.collapse.web.pattern.RequestCollapseGroup;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpMethod;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import reactor.core.publisher.Mono;

import java.util.concurrent.CompletableFuture;

/**
 * @author icodening
 * @date 2023.06.23
 */
public class CollapseExchangeFilterFunction implements ExchangeFilterFunction {

    private static final DataBufferFactory HEAP_BUFFER_FACTORY = DefaultDataBufferFactory.sharedInstance;

    private static final String IDENTIFIER = CollapseExchangeFilterFunction.class.getName();

    private FutureCallableGroupCollapseExecutor futureCallableGroupCollapseExecutor;

    @Nullable
    protected CollapseGroupResolver collapseGroupResolver;

    public CollapseExchangeFilterFunction() {

    }

    public CollapseExchangeFilterFunction(FutureCallableGroupCollapseExecutor futureCallableGroupCollapseExecutor) {
        this.futureCallableGroupCollapseExecutor = futureCallableGroupCollapseExecutor;
    }

    public void setCollapseGroupResolver(CollapseGroupResolver collapseGroupResolver) {
        this.collapseGroupResolver = collapseGroupResolver;
    }

    public void setFutureCallableGroupCollapseExecutor(FutureCallableGroupCollapseExecutor futureCallableGroupCollapseExecutor) {
        this.futureCallableGroupCollapseExecutor = futureCallableGroupCollapseExecutor;
    }

    @Override
    public Mono<ClientResponse> filter(ClientRequest request, ExchangeFunction next) {
        if (!allowCollapse(request)) {
            return next.exchange(request);
        }
        RequestCollapseGroup requestCollapseGroup = findCollapseGroup(request);
        if (requestCollapseGroup == null) {
            return next.exchange(request);
        }
        requestCollapseGroup.setIdentifier(IDENTIFIER);
        return Mono.fromFuture(collapseExecute(requestCollapseGroup, request, next));
    }

    protected RequestCollapseGroup findCollapseGroup(ClientRequest request) {
        if (collapseGroupResolver == null) {
            return null;
        }
        return collapseGroupResolver.resolve(new WebClientRequestAttributes(request));
    }

    protected boolean allowCollapse(ClientRequest request) {
        HttpMethod method = request.method();
        return HttpMethod.GET.equals(method);
    }

    private CompletableFuture<ClientResponse> collapseExecute(RequestCollapseGroup requestCollapseGroup, ClientRequest request, ExchangeFunction next) {
        FutureCallableGroupCollapseExecutor futureCallableGroupCollapseExecutor = this.futureCallableGroupCollapseExecutor;
        Assert.notNull(futureCallableGroupCollapseExecutor, "futureCallableGroupCollapseExecutor must be not null.");
        return futureCallableGroupCollapseExecutor.execute(requestCollapseGroup,
                () -> next.exchange(request)
                        .map(clientResponse ->
                                clientResponse.mutate()
                                        .body(dataBufferFlux ->
                                                dataBufferFlux.map(this::toHeapBuffer)
                                                        .cache())
                                        .build())
                        .toFuture());
    }

    private DataBuffer toHeapBuffer(DataBuffer buffer) {
        byte[] data = readDataBuffer(buffer);
        return HEAP_BUFFER_FACTORY.wrap(data);
    }

    private byte[] readDataBuffer(DataBuffer dataBuffer) {
        int readableByteCount = dataBuffer.readableByteCount();
        byte[] data = new byte[dataBuffer.readableByteCount()];
        dataBuffer.read(data, 0, readableByteCount);
        DataBufferUtils.release(dataBuffer);
        return data;
    }
}
