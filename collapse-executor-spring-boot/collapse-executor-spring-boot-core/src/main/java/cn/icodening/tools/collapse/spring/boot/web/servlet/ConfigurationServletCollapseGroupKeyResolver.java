package cn.icodening.tools.collapse.spring.boot.web.servlet;

import javax.servlet.http.HttpServletRequest;
import java.util.Set;

/**
 * @author icodening
 * @date 2023.05.25
 */
public class ConfigurationServletCollapseGroupKeyResolver implements ServletCollapseGroupKeyResolver {

    private final CollapseServletProperties collapseServletProperties;

    public ConfigurationServletCollapseGroupKeyResolver(CollapseServletProperties collapseServletProperties) {
        this.collapseServletProperties = collapseServletProperties;
    }

    @Override
    public ServletCollapseGroupKey resolveGroupKey(HttpServletRequest httpServletRequest) {
        for (CollapseServletProperties.CollapseGroup collapseGroup : this.collapseServletProperties.getCollapseGroups()) {
            Set<String> uris = collapseGroup.getUris();
            String requestURI = httpServletRequest.getRequestURI();
            for (String uri : uris) {
                if (requestURI.startsWith(uri)) {
                    String groupPolicyName = collapseGroup.getCollapsePolicyName();
                    CollapseServletProperties.CollapsePolicy collapsePolicy = this.collapseServletProperties.getCollapsePolicies().get(groupPolicyName);
                    if (collapsePolicy == null) {
                        return null;
                    }
                    ServletCollapseGroupKey servletCollapseGroupKey = new ServletCollapseGroupKey();
                    servletCollapseGroupKey.setMethod(httpServletRequest.getMethod());
                    servletCollapseGroupKey.setPath(httpServletRequest.getRequestURI());
                    for (String headerName : collapsePolicy.getCollapseRequestHeaders()) {
                        servletCollapseGroupKey.getHeaders().put(headerName, httpServletRequest.getHeader(headerName));
                    }
                    if (collapsePolicy.isCollapseQueryString()) {
                        servletCollapseGroupKey.setQuery(httpServletRequest.getQueryString());
                    }
                    return servletCollapseGroupKey;
                }
            }
        }
        return null;
    }
}
