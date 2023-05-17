package cn.icodening.tools.collapse.spring.boot.web.client;

import cn.icodening.tools.collapse.core.CallableGroupCollapseExecutor;
import cn.icodening.tools.collapse.core.ListeningBundleCollector;
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

    private final CallableGroupCollapseExecutor callableGroupCollapseExecutor;

    public CollapseHttpRequestInterceptor(ListeningBundleCollector listeningBundleCollector) {
        this.callableGroupCollapseExecutor = new CallableGroupCollapseExecutor(listeningBundleCollector);
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        HttpMethod method = request.getMethod();
        if (!HttpMethod.GET.equals(method)) {
            return execution.execute(request, body);
        }
        try {
            return this.callableGroupCollapseExecutor.execute(request.getURI(), () -> repeatableReadResponse(execution.execute(request, body)));
        } catch (RuntimeException | IOException e) {
            throw e;
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }

    private ClientHttpResponse repeatableReadResponse(ClientHttpResponse origin) throws IOException {
        int rawStatusCode = origin.getRawStatusCode();
        HttpStatus statusCode = origin.getStatusCode();
        String statusText = origin.getStatusText();
        HttpHeaders headers = origin.getHeaders();
        return new RepeatableReadResponse(rawStatusCode, statusCode, statusText, headers, StreamUtils.copyToByteArray(origin.getBody()));
    }

}
