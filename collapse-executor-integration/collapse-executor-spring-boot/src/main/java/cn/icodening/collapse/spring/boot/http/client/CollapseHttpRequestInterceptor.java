package cn.icodening.collapse.spring.boot.http.client;

import cn.icodening.collapse.core.ListenableCollector;
import cn.icodening.collapse.core.support.BlockingCallableGroupCollapseExecutor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StreamUtils;

import java.io.IOException;

/**
 * @author icodening
 * @date 2023.05.16
 */
public class CollapseHttpRequestInterceptor implements ClientHttpRequestInterceptor {

    private static final String GROUP_PREFIX = CollapseHttpRequestInterceptor.class.getName() + ".GET ";

    private final BlockingCallableGroupCollapseExecutor blockingCallableGroupCollapseExecutor;

    public CollapseHttpRequestInterceptor(ListenableCollector listenableCollector) {
        this.blockingCallableGroupCollapseExecutor = new BlockingCallableGroupCollapseExecutor(listenableCollector);
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        if (!allowCollapse(request)) {
            return execution.execute(request, body);
        }
        try {
            return this.blockingCallableGroupCollapseExecutor.execute(GROUP_PREFIX + request.getURI(), () -> repeatableReadResponse(execution.execute(request, body)));
        } catch (RuntimeException | IOException e) {
            throw e;
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
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
