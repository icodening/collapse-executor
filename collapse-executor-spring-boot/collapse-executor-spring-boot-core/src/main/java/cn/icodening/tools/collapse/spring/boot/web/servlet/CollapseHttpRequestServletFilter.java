package cn.icodening.tools.collapse.spring.boot.web.servlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.AsyncContext;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

/**
 * @author icodening
 * @date 2023.05.20
 */
class CollapseHttpRequestServletFilter extends OncePerRequestFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(CollapseHttpRequestServletFilter.class);

    private final AsyncServletExecutor asyncServletExecutor;

    private final ServletCollapseGroupKeyResolver servletCollapseGroupKeyResolver;

    public CollapseHttpRequestServletFilter(AsyncServletExecutor asyncServletExecutor, ServletCollapseGroupKeyResolver servletCollapseGroupKeyResolver) {
        this.asyncServletExecutor = asyncServletExecutor;
        this.servletCollapseGroupKeyResolver = servletCollapseGroupKeyResolver;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain chain) throws ServletException, IOException {
        ServletCollapseGroupKey groupKey = servletCollapseGroupKeyResolver.resolveGroupKey(httpServletRequest);
        if (groupKey == null) {
            chain.doFilter(httpServletRequest, httpServletResponse);
            return;
        }
        AsyncContext asyncContext = httpServletRequest.startAsync(httpServletRequest, new RecordableServletResponse(httpServletResponse));
        ServletCollapseRequest servletCollapseRequest = new ServletCollapseRequest(groupKey, asyncContext);
        try {
            CompletableFuture<ServletCollapseResponse> future = asyncServletExecutor.execute(servletCollapseRequest);
            future.whenComplete((collapseResponse, throwable) -> {
                try {
                    if (throwable != null) {
                        throw throwable;
                    }
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
}
