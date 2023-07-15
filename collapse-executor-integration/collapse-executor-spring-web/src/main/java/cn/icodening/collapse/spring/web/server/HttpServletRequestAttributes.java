package cn.icodening.collapse.spring.web.server;

import cn.icodening.collapse.spring.web.pattern.RequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

/**
 * @author icodening
 * @date 2023.06.29
 */
public class HttpServletRequestAttributes implements RequestAttributes {

    private final String httpMethod;

    private final URI uri;

    private final Map<String, List<String>> httpHeaders;

    public HttpServletRequestAttributes(final HttpServletRequest httpServletRequest) {
        Objects.requireNonNull(httpServletRequest, "httpServletRequest must be not null.");
        this.httpMethod = httpServletRequest.getMethod();
        this.uri = createURI(httpServletRequest);
        this.httpHeaders = resolveHttpHeaders(httpServletRequest);
    }

    private URI createURI(HttpServletRequest httpServletRequest) {
        String url = httpServletRequest.getRequestURL().toString();
        String queryString = httpServletRequest.getQueryString();
        String query = queryString == null ? "" : "?" + queryString;
        String fullURI = url + query;
        return URI.create(fullURI);
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
    public Map<String, List<String>> getHeaders() {
        return httpHeaders;
    }

    private static Map<String, List<String>> resolveHttpHeaders(HttpServletRequest httpServletRequest) {
        Map<String, List<String>> headers = new TreeMap<>(String::compareToIgnoreCase);
        Enumeration<String> headerNames = httpServletRequest.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            Enumeration<String> headerValues = httpServletRequest.getHeaders(headerName);
            while (headerValues.hasMoreElements()) {
                String headerValue = headerValues.nextElement();
                headers.computeIfAbsent(headerName, (key) -> new ArrayList<>()).add(headerValue);
            }
        }
        return headers;
    }
}
