package cn.icodening.collapse.spring.web.server;

import cn.icodening.collapse.spring.web.pattern.CollapseGroupResolver;
import cn.icodening.collapse.spring.web.pattern.RequestCollapseGroup;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.AsyncContext;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * @author icodening
 * @date 2023.05.20
 */
public class CollapseHttpRequestServletFilter extends OncePerRequestFilter {

    private static final Logger LOGGER = Logger.getLogger(CollapseHttpRequestServletFilter.class.getName());

    private AsyncServletExecutor asyncServletExecutor;

    private CollapseGroupResolver collapseGroupResolver;

    public CollapseHttpRequestServletFilter() {
    }

    public CollapseHttpRequestServletFilter(AsyncServletExecutor asyncServletExecutor) {
        this.asyncServletExecutor = asyncServletExecutor;
    }

    public void setAsyncServletExecutor(AsyncServletExecutor asyncServletExecutor) {
        this.asyncServletExecutor = asyncServletExecutor;
    }

    public void setCollapseGroupResolver(CollapseGroupResolver collapseGroupResolver) {
        this.collapseGroupResolver = collapseGroupResolver;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain chain) throws ServletException, IOException {
        if (!allowCollapse(httpServletRequest)) {
            chain.doFilter(httpServletRequest, httpServletResponse);
            return;
        }
        RequestCollapseGroup requestCollapseGroup = findCollapseGroup(httpServletRequest);
        if (requestCollapseGroup == null) {
            chain.doFilter(httpServletRequest, httpServletResponse);
            return;
        }
        AsyncContext asyncContext = httpServletRequest.startAsync(httpServletRequest, new RecordableServletResponse(httpServletResponse));
        ServletCollapseRequest servletCollapseRequest = new ServletCollapseRequest(requestCollapseGroup, asyncContext);
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
                LogRecord logRecord = new LogRecord(Level.SEVERE, "Processing response failed.");
                logRecord.setThrown(e);
                LOGGER.log(logRecord);
            } finally {
                asyncContext.complete();
            }
        });
    }

    protected boolean allowCollapse(HttpServletRequest httpServletRequest) {
        return "GET".equals(httpServletRequest.getMethod());
    }

    protected RequestCollapseGroup findCollapseGroup(HttpServletRequest request) {
        if (collapseGroupResolver == null) {
            return null;
        }
        return collapseGroupResolver.resolve(new HttpServletRequestAttributes(request));
    }
}
