package cn.icodening.tools.collapse.spring.boot.web.servlet;

import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Set;

/**
 * @author icodening
 * @date 2023.05.25
 */
public class ConfigurationServletCollapseGroupKeyResolver implements ServletCollapseGroupKeyResolver {

    private final CollapseServletProperties collapseServletProperties;

    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    public ConfigurationServletCollapseGroupKeyResolver(CollapseServletProperties collapseServletProperties) {
        this.collapseServletProperties = collapseServletProperties;
    }

    @Override
    public ServletCollapseGroupKey resolveGroupKey(HttpServletRequest httpServletRequest) {
        for (CollapseServletProperties.CollapseGroup collapseGroup : this.collapseServletProperties.getCollapseGroups()) {
            Set<String> uris = collapseGroup.getUris();
            for (String uri : uris) {
                if (isMatch(httpServletRequest, uri)) {
                    String groupPolicyName = collapseGroup.getCollapsePolicyName();
                    CollapseServletProperties.CollapsePolicy collapsePolicy = this.collapseServletProperties.getCollapsePolicies().get(groupPolicyName);
                    if (collapsePolicy == null) {
                        return null;
                    }
                    ServletCollapseGroupKey servletCollapseGroupKey = new ServletCollapseGroupKey();
                    servletCollapseGroupKey.setMethod(httpServletRequest.getMethod());
                    servletCollapseGroupKey.setPath(httpServletRequest.getRequestURI());
                    for (String headerName : collapsePolicy.getCollapseRequestHeaders()) {
                        String header = httpServletRequest.getHeader(headerName);
                        if (StringUtils.hasText(header)) {
                            servletCollapseGroupKey.getHeaders().put(headerName, header);
                        }
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

    private boolean isMatch(HttpServletRequest request, String configURI) {
        return antPathMatcher.match(configURI, request.getRequestURI());
    }
}
