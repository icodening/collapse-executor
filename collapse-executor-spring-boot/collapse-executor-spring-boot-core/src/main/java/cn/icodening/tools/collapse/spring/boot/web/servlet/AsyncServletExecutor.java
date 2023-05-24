package cn.icodening.tools.collapse.spring.boot.web.servlet;

import cn.icodening.tools.collapse.core.Bundle;
import cn.icodening.tools.collapse.core.CollapseExecutorAsyncSupport;
import cn.icodening.tools.collapse.core.Input;
import cn.icodening.tools.collapse.core.ListeningBundleCollector;

import javax.servlet.AsyncContext;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.ServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @author icodening
 * @date 2023.05.22
 */
public class AsyncServletExecutor extends CollapseExecutorAsyncSupport<ServletCollapseRequest, ServletCollapseResponse, CompletableFuture<ServletCollapseResponse>> {

    public AsyncServletExecutor(ListeningBundleCollector collector) {
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
    protected void bindingOutput(CompletableFuture<ServletCollapseResponse> responseCompletableFuture,
                                 List<Bundle<ServletCollapseRequest, CompletableFuture<ServletCollapseResponse>>> bundles) {
        for (Bundle<ServletCollapseRequest, CompletableFuture<ServletCollapseResponse>> bundle : bundles) {
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
