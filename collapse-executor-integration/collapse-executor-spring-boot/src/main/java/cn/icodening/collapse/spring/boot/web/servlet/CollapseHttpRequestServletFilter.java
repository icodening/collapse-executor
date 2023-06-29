package cn.icodening.collapse.spring.boot.web.servlet;

import cn.icodening.collapse.spring.boot.pattern.ConfigurationCollapseGroupResolver;
import cn.icodening.collapse.spring.boot.pattern.RequestCollapseGroup;
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

    private final ConfigurationCollapseGroupResolver servletCollapseGroupKeyResolver;

    public CollapseHttpRequestServletFilter(AsyncServletExecutor asyncServletExecutor, ConfigurationCollapseGroupResolver servletCollapseGroupKeyResolver) {
        this.asyncServletExecutor = asyncServletExecutor;
        this.servletCollapseGroupKeyResolver = servletCollapseGroupKeyResolver;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain chain) throws ServletException, IOException {
        RequestCollapseGroup groupKey = servletCollapseGroupKeyResolver.resolve(new HttpServletRequestAttributes(httpServletRequest));
        if (groupKey == null) {
            chain.doFilter(httpServletRequest, httpServletResponse);
            return;
        }
        AsyncContext asyncContext = httpServletRequest.startAsync(httpServletRequest, new RecordableServletResponse(httpServletResponse));
        ServletCollapseRequest servletCollapseRequest = new ServletCollapseRequest(groupKey, asyncContext);
        CompletableFuture<ServletCollapseResponse> future = asyncServletExecutor.execute(servletCollapseRequest);
        future.whenComplete((collapseResponse, throwable) -> {
            try {
                if (throwable != null) {
                    throw throwable;
                }
                RecordableServletOutputStream recordableServletOutputStream = collapseResponse.getRecordableServletOutputStream();
                byte[] data = recordableServletOutputStream.getRecordBytes();
                HttpServletResponse response = collapseResponse.getResponse();
                ServletOutputStream actualResponseOutputStream = httpServletResponse.getOutputStream();
                httpServletResponse.setContentType(response.getContentType());
                httpServletResponse.setStatus(response.getStatus());
                httpServletResponse.setContentLength(data.length);
                actualResponseOutputStream.write(data);
            } catch (Throwable e) {
                LOGGER.error("Processing response failed.", e);
            } finally {
                asyncContext.complete();
            }
        });
    }
}
