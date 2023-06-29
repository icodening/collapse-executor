package cn.icodening.collapse.spring.boot.http.reactive;

import cn.icodening.collapse.spring.boot.pattern.RequestAttributes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.util.Assert;
import org.springframework.web.reactive.function.client.ClientRequest;

import java.net.URI;

/**
 * @author icodening
 * @date 2023.06.29
 */
public class WebClientRequestAttributes implements RequestAttributes {

    private final ClientRequest clientRequest;

    public WebClientRequestAttributes(ClientRequest clientRequest) {
        Assert.notNull(clientRequest, "clientRequest must be not null.");
        this.clientRequest = clientRequest;
    }

    @Override
    public HttpMethod getMethod() {
        return clientRequest.method();
    }

    @Override
    public URI getURI() {
        return clientRequest.url();
    }

    @Override
    public HttpHeaders getHeaders() {
        return clientRequest.headers();
    }
}
