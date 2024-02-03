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

import com.icodening.collapse.web.pattern.CollapseGroupResolver;
import com.icodening.collapse.web.pattern.RequestCollapseGroup;

import javax.servlet.AsyncContext;
import javax.servlet.DispatcherType;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpFilter;
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
public class CollapseHttpRequestServletFilter extends HttpFilter {

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
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        if (skipDispatch(request)) {
            chain.doFilter(request, response);
            return;
        }
        doFilterInternal(request, response, chain);
    }

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

    private boolean skipDispatch(ServletRequest request) {
        return DispatcherType.ASYNC.equals(request.getDispatcherType());
    }
}
