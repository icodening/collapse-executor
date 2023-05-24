package cn.icodening.tools.collapse.spring.boot.web.servlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.AsyncContext;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author icodening
 * @date 2023.05.20
 */
class CollapseHttpRequestServletFilter extends OncePerRequestFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(CollapseHttpRequestServletFilter.class);

    private final AsyncServletExecutor asyncServletExecutor;

    private final HttpServletRequestMatcher httpServletRequestMatcher;

    public CollapseHttpRequestServletFilter(AsyncServletExecutor asyncServletExecutor, HttpServletRequestMatcher httpServletRequestMatcher) {
        this.asyncServletExecutor = asyncServletExecutor;
        this.httpServletRequestMatcher = httpServletRequestMatcher;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain chain) throws ServletException, IOException {
        boolean match = httpServletRequestMatcher.match(httpServletRequest);
        if (!match) {
            chain.doFilter(httpServletRequest, httpServletResponse);
            return;
        }
        String requestURI = httpServletRequest.getRequestURI();
        String queryString = httpServletRequest.getQueryString();
        String group = queryString == null ? requestURI : requestURI + "?" + queryString;
        AsyncContext asyncContext = httpServletRequest.startAsync(httpServletRequest, new RecordableServletResponse(httpServletResponse));
        AtomicBoolean completed = addStatusListener(asyncContext);
        ServletCollapseRequest servletCollapseRequest = new ServletCollapseRequest(group, asyncContext);
        try {
            CompletableFuture<ServletCollapseResponse> future = asyncServletExecutor.execute(servletCollapseRequest);
            future.whenComplete((collapseResponse, throwable) -> {
                if (completed.get()) {
                    return;
                }
                try {
                    RecordableServletOutputStream recordableServletOutputStream = collapseResponse.getRecordableServletOutputStream();
                    byte[] data = recordableServletOutputStream.getRecordBytes();
                    HttpServletResponse response = collapseResponse.getResponse();
                    ServletOutputStream realResponseOutputStream = httpServletResponse.getOutputStream();
                    httpServletResponse.setContentType(response.getContentType());
                    httpServletResponse.setStatus(response.getStatus());
                    httpServletResponse.setContentLength(data.length);
                    realResponseOutputStream.write(data);
                } catch (Throwable e) {
                    LOGGER.error("Processing response failed.", e);
                } finally {
                    asyncContext.complete();
                }
            });
        } catch (Throwable e) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            }
            throw new RuntimeException(e);
        }
    }

    private AtomicBoolean addStatusListener(AsyncContext asyncContext) {
        AtomicBoolean completed = new AtomicBoolean(false);
        asyncContext.addListener(new AsyncListener() {
            @Override
            public void onComplete(AsyncEvent event) throws IOException {
                completed.set(true);
            }

            @Override
            public void onTimeout(AsyncEvent event) throws IOException {

            }

            @Override
            public void onError(AsyncEvent event) throws IOException {
                completed.set(true);
            }

            @Override
            public void onStartAsync(AsyncEvent event) throws IOException {

            }
        });
        return completed;
    }
}
