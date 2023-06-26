package cn.icodening.collapse.spring.boot.http.reactive;

import cn.icodening.collapse.core.ListenableCollector;
import cn.icodening.collapse.core.support.FutureCallableGroupCollapseExecutor;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
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

    private static final DataBufferFactory HEAP_BUFFER_FACTORY = DefaultDataBufferFactory.sharedInstance;

    private static final String GROUP_PREFIX = CollapseExchangeFilterFunction.class.getName() + ".GET ";

    private final FutureCallableGroupCollapseExecutor collapseExecutor;

    public CollapseExchangeFilterFunction(ListenableCollector listenableCollector) {
        this(new FutureCallableGroupCollapseExecutor(listenableCollector));
    }

    public CollapseExchangeFilterFunction(FutureCallableGroupCollapseExecutor futureCallableGroupCollapseExecutor) {
        this.collapseExecutor = futureCallableGroupCollapseExecutor;
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
                                                dataBufferFlux.map(this::toHeapBuffer)
                                                        .cache())
                                        .build())
                        .toFuture());
    }

    private DataBuffer toHeapBuffer(DataBuffer buffer){
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
