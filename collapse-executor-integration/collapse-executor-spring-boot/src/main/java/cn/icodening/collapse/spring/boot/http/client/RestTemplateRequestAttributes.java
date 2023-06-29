package cn.icodening.collapse.spring.boot.http.client;

import cn.icodening.collapse.spring.boot.pattern.RequestAttributes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.util.Assert;

import java.net.URI;

/**
 * @author icodening
 * @date 2023.06.29
 */
public class RestTemplateRequestAttributes implements RequestAttributes {

    private final HttpRequest request;

    public RestTemplateRequestAttributes(HttpRequest request) {
        Assert.notNull(request, "request must be not null.");
        this.request = request;
    }

    @Override
    public HttpMethod getMethod() {
        return request.getMethod();
    }

    @Override
    public URI getURI() {
        return request.getURI();
    }

    @Override
    public HttpHeaders getHeaders() {
        return request.getHeaders();
    }
}
