package cn.icodening.collapse.spring.boot.web.servlet;

import javax.servlet.http.HttpServletRequest;

/**
 * @author icodening
 * @date 2023.05.25
 */
public interface ServletCollapseGroupKeyResolver {

    ServletCollapseGroupKey resolveGroupKey(HttpServletRequest httpServletRequest);

}
