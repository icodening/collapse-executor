package cn.icodening.collapse.spring.boot.web.client;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * @author icodening
 * @date 2023.05.16
 */
class RepeatableReadResponse implements ClientHttpResponse {

    private final int rawStatusCode;
    private final HttpStatus statusCode;
    private final String statusText;
    private final HttpHeaders headers;
    private final byte[] body;

    RepeatableReadResponse(int rawStatusCode, HttpStatus statusCode, String statusText, HttpHeaders headers, byte[] body) {
        this.rawStatusCode = rawStatusCode;
        this.statusCode = statusCode;
        this.statusText = statusText;
        this.headers = headers;
        this.body = body;
    }

    @Override
    public void close() {
        //No op
    }

    @Override
    public int getRawStatusCode() {
        return rawStatusCode;
    }

    @Override
    public HttpStatus getStatusCode() {
        return statusCode;
    }

    @Override
    public String getStatusText() {
        return statusText;
    }

    @Override
    public HttpHeaders getHeaders() {
        return headers;
    }

    @Override
    public InputStream getBody() {
        return new ByteArrayInputStream(body);
    }
}