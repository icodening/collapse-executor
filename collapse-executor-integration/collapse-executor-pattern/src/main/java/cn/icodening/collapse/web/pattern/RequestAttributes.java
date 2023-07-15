package cn.icodening.collapse.web.pattern;

import java.net.URI;
import java.util.List;
import java.util.Map;

/**
 * @author icodening
 * @date 2023.06.29
 */
public interface RequestAttributes {

    /**
     * get request method type.
     * eg. GET POST PUT ....
     *
     * @return request method
     */
    String getMethod();

    /**
     * get request uri
     *
     * @return uri
     */
    URI getURI();

    /**
     * get request header, it's a case insensitive map.
     *
     * @return request header.
     */
    Map<String, List<String>> getHeaders();

}
