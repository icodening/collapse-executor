package cn.icodening.collapse.spring.boot.http.reactive;

import cn.icodening.collapse.core.ListenableCollector;
import cn.icodening.collapse.core.support.FutureCallableGroupCollapseExecutor;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpMethod;
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

    private static final String GROUP_PREFIX = CollapseExchangeFilterFunction.class.getName() + ".GET ";

    private final FutureCallableGroupCollapseExecutor collapseExecutor;

    public CollapseExchangeFilterFunction(ListenableCollector listenableCollector) {
        this.collapseExecutor = new FutureCallableGroupCollapseExecutor(listenableCollector);
    }

    @Override
    public Mono<ClientResponse> filter(ClientRequest request, ExchangeFunction next) {
        if (!allowCollapse(request)) {
            return next.exchange(request);
        }
        return Mono.fromFuture(collapseExecute(request, next));
    }

    protected boolean allowCollapse(ClientRequest request) {
        HttpMethod method = request.method();
        return HttpMethod.GET.equals(method);
    }

    private CompletableFuture<ClientResponse> collapseExecute(ClientRequest request, ExchangeFunction next) {
        return collapseExecutor.execute(GROUP_PREFIX + request.url(),
                () -> next.exchange(request)
                        .map(clientResponse ->
                                clientResponse.mutate()
                                        .body(dataBufferFlux ->
                                                dataBufferFlux.map(this::duplicateDataBuffer)
                                                        .cache())
                                        .build())
                        .toFuture());
    }

    private DataBuffer duplicateDataBuffer(DataBuffer srcDataBuffer) {
        DataBufferFactory bufferFactory = srcDataBuffer.factory();
        DataBuffer destDataBuffer = bufferFactory.allocateBuffer(srcDataBuffer.readableByteCount());
        destDataBuffer.write(srcDataBuffer);
        DataBufferUtils.release(srcDataBuffer);
        return destDataBuffer;
    }
}
