package cn.icodening.collapse.spring.web.client;

import cn.icodening.collapse.core.support.BlockingCallableGroupCollapseExecutor;
import cn.icodening.collapse.web.pattern.CollapseGroupResolver;
import cn.icodening.collapse.web.pattern.RequestCollapseGroup;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StreamUtils;

import java.io.IOException;

/**
 * @author icodening
 * @date 2023.05.16
 */
public class CollapseHttpRequestInterceptor implements ClientHttpRequestInterceptor {

    private static final String IDENTIFIER = CollapseHttpRequestInterceptor.class.getName();

    private BlockingCallableGroupCollapseExecutor blockingCallableGroupCollapseExecutor;

    @Nullable
    protected CollapseGroupResolver collapseGroupResolver;

    public CollapseHttpRequestInterceptor() {

    }

    public CollapseHttpRequestInterceptor(BlockingCallableGroupCollapseExecutor blockingCallableGroupCollapseExecutor) {
        this.blockingCallableGroupCollapseExecutor = blockingCallableGroupCollapseExecutor;
    }

    public void setCollapseGroupResolver(@Nullable CollapseGroupResolver collapseGroupResolver) {
        this.collapseGroupResolver = collapseGroupResolver;
    }

    public void setBlockingCallableGroupCollapseExecutor(BlockingCallableGroupCollapseExecutor blockingCallableGroupCollapseExecutor) {
        this.blockingCallableGroupCollapseExecutor = blockingCallableGroupCollapseExecutor;
    }

    @Override
    @NonNull
    public ClientHttpResponse intercept(@NonNull HttpRequest request, @NonNull byte[] body, @NonNull ClientHttpRequestExecution execution) throws IOException {
        if (!allowCollapse(request)) {
            return execution.execute(request, body);
        }
        try {
            RequestCollapseGroup requestCollapseGroup = findCollapseGroup(request);
            if (requestCollapseGroup == null) {
                return execution.execute(request, body);
            }
            requestCollapseGroup.setIdentifier(IDENTIFIER);
            BlockingCallableGroupCollapseExecutor collapseExecutor = this.blockingCallableGroupCollapseExecutor;
            Assert.notNull(collapseExecutor, "blockingCallableGroupCollapseExecutor must be not null.");
            return collapseExecutor.execute(requestCollapseGroup, () -> repeatableReadResponse(execution.execute(request, body)));
        } catch (RuntimeException | IOException e) {
            throw e;
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }

    protected RequestCollapseGroup findCollapseGroup(HttpRequest request) {
        if (collapseGroupResolver == null) {
            return null;
        }
        return collapseGroupResolver.resolve(new RestTemplateRequestAttributes(request));
    }

    protected boolean allowCollapse(HttpRequest request) {
        HttpMethod method = request.getMethod();
        return HttpMethod.GET.equals(method);
    }

    private ClientHttpResponse repeatableReadResponse(ClientHttpResponse origin) throws IOException {
        int rawStatusCode = origin.getRawStatusCode();
        HttpStatus statusCode = origin.getStatusCode();
        String statusText = origin.getStatusText();
        HttpHeaders headers = origin.getHeaders();
        return new RepeatableReadResponse(rawStatusCode, statusCode, statusText, headers, StreamUtils.copyToByteArray(origin.getBody()));
    }

}
