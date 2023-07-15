package cn.icodening.collapse.spring.web.client.reactive;

import cn.icodening.collapse.web.pattern.RequestAttributes;
import org.springframework.http.HttpHeaders;
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
    public String getMethod() {
        return clientRequest.method().name();
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
