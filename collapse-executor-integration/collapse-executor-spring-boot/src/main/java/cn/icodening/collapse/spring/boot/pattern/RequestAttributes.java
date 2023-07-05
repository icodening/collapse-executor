package cn.icodening.collapse.spring.boot.pattern;

import org.springframework.http.HttpHeaders;

import java.net.URI;

/**
 * @author icodening
 * @date 2023.06.29
 */
public interface RequestAttributes {

    String getMethod();

    URI getURI();

    HttpHeaders getHeaders();

}
