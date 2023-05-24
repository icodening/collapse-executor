package cn.icodening.tools.collapse.spring.boot.web.servlet;

import javax.servlet.http.HttpServletRequest;

/**
 * @author icodening
 * @date 2023.05.23
 */
@FunctionalInterface
public interface HttpServletRequestMatcher {

    boolean match(HttpServletRequest httpServletRequest);

}
