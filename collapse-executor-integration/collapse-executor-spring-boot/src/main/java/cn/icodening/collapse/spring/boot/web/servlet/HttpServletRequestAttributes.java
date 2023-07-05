package cn.icodening.collapse.spring.boot.web.servlet;

import cn.icodening.collapse.spring.boot.pattern.RequestAttributes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.util.Assert;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.Enumeration;

/**
 * @author icodening
 * @date 2023.06.29
 */
public class HttpServletRequestAttributes implements RequestAttributes {

    private final String httpMethod;

    private final URI uri;

    private final HttpHeaders httpHeaders;

    public HttpServletRequestAttributes(final HttpServletRequest httpServletRequest) {
        Assert.notNull(httpServletRequest, "httpServletRequest must be not null.");
        this.httpMethod = HttpMethod.valueOf(httpServletRequest.getMethod()).name();
        this.uri = UriComponentsBuilder.fromHttpUrl(httpServletRequest.getRequestURL().toString()).query(httpServletRequest.getQueryString()).build().toUri();
        this.httpHeaders = resolveHttpHeaders(httpServletRequest);
    }

    @Override
    public String getMethod() {
        return httpMethod;
    }

    @Override
    public URI getURI() {
        return uri;
    }

    @Override
    public HttpHeaders getHeaders() {
        return httpHeaders;
    }

    private static HttpHeaders resolveHttpHeaders(HttpServletRequest httpServletRequest) {
        HttpHeaders headers = new HttpHeaders();
        Enumeration<String> headerNames = httpServletRequest.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            Enumeration<String> headerValues = httpServletRequest.getHeaders(headerName);
            while (headerValues.hasMoreElements()) {
                String headerValue = headerValues.nextElement();
                headers.add(headerName, headerValue);
            }
        }
        return headers;
    }
}
