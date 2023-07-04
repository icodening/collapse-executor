package cn.icodening.collapse.spring.boot.web.servlet;

import cn.icodening.collapse.core.Bundle;
import cn.icodening.collapse.core.CollapseExecutorAsyncSupport;
import cn.icodening.collapse.core.Input;
import cn.icodening.collapse.core.ListeningCollector;

import javax.servlet.AsyncContext;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.ServletResponse;
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
            RecordableServletResponse repeatableReadServletResponse = (RecordableServletResponse) suppliedResponse;
            RecordableServletOutputStream outputStream = (RecordableServletOutputStream) repeatableReadServletResponse.getOutputStream();
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
    }
}
