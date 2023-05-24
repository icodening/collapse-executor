package cn.icodening.tools.collapse.spring.boot.web.servlet;

import javax.servlet.http.HttpServletRequest;

/**
 * @author icodening
 * @date 2023.05.23
 */
public class ConfigurationRequestMatcher implements HttpServletRequestMatcher {

    private CollapseServletProperties collapseServletProperties;

    public ConfigurationRequestMatcher() {
    }

    public ConfigurationRequestMatcher(CollapseServletProperties collapseServletProperties) {
        this.collapseServletProperties = collapseServletProperties;
    }

    public void setCollapseServletProperties(CollapseServletProperties collapseServletProperties) {
        this.collapseServletProperties = collapseServletProperties;
    }

    @Override
    public boolean match(HttpServletRequest httpServletRequest) {
        String method = httpServletRequest.getMethod();
        if (!"GET".equals(method)) {
            return false;
        }
        String requestURI = httpServletRequest.getRequestURI();
        boolean found = false;
        for (String uri : collapseServletProperties.getUris()) {
            if (requestURI.startsWith(uri)) {
                found = true;
                break;
            }
        }
        return found;
    }
}
