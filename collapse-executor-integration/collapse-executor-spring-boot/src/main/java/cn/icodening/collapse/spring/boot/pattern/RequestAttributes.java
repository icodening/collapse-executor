package cn.icodening.collapse.spring.boot.pattern;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

import java.net.URI;

/**
 * @author icodening
 * @date 2023.06.29
 */
public interface RequestAttributes {

    HttpMethod getMethod();

    URI getURI();

    HttpHeaders getHeaders();

}
