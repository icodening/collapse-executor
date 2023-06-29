package cn.icodening.collapse.spring.boot.pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.PathContainer;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.util.pattern.PathPatternParser;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author icodening
 * @date 2023.06.29
 */
public class ConfigurationCollapseGroupResolver implements CollapseGroupResolver {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationCollapseGroupResolver.class);

    private final CollapseDefinitionProperties collapseDefinitionProperties;

    private static final PathPatternParser PATTERN_PARSER = PathPatternParser.defaultInstance;

    public ConfigurationCollapseGroupResolver(CollapseDefinitionProperties collapseDefinitionProperties) {
        this.collapseDefinitionProperties = collapseDefinitionProperties;
    }

    @Override
    public RequestCollapseGroup resolve(RequestAttributes requestAttributes) {
        if (!collapseDefinitionProperties.isEnabled()) {
            return null;
        }
        for (CollapseGroupDefinition definition : this.collapseDefinitionProperties.getCollapseGroups()) {
            Set<String> uris = definition.getUris();
            for (String uri : uris) {
                if (isMatch(requestAttributes, uri)) {
                    String groupPolicyName = definition.getCollapsePolicyName();
                    CollapsePolicyDefinition collapsePolicy = this.collapseDefinitionProperties.getCollapsePolicies().get(groupPolicyName);
                    if (collapsePolicy == null) {
                        LOGGER.debug("['{}' not found, please check configuration.]", groupPolicyName);
                        return null;
                    }
                    RequestCollapseGroup requestCollapseGroup = new RequestCollapseGroup();
                    //1. method
                    requestCollapseGroup.setMethod(requestAttributes.getMethod().toString());
                    URI requestURI = requestAttributes.getURI();
                    //2. path
                    requestCollapseGroup.setPath(requestURI.getPath());
                    //3. header
                    for (String headerName : collapsePolicy.getCollapseRequestHeaders()) {
                        List<String> header = requestAttributes.getHeaders().get(headerName);
                        if (!CollectionUtils.isEmpty(header)) {
                            requestCollapseGroup.getHeaders().put(headerName, header);
                        }
                    }
                    //4.query
                    Map<String, List<String>> queryMap = queryStringToMap(requestURI.getQuery());
                    for (String collapseRequestQuery : collapsePolicy.getCollapseRequestQueries()) {
                        List<String> value = queryMap.get(collapseRequestQuery);
                        if (!CollectionUtils.isEmpty(value)) {
                            requestCollapseGroup.getQueries().put(collapseRequestQuery, value);
                        }
                    }
                    return requestCollapseGroup;
                }
            }
        }
        return null;
    }

    private boolean isMatch(RequestAttributes request, String configURI) {
        String path = request.getURI().getPath();
        return PATTERN_PARSER.parse(configURI)
                .matches(PathContainer.parsePath(path));
    }

    private Map<String, List<String>> queryStringToMap(String queryString) {
        if (!StringUtils.hasText(queryString)) {
            return Collections.emptyMap();
        }
        String[] queryKVs = queryString.split("&");
        Map<String, List<String>> queryMap = new HashMap<>();
        for (String queryKV : queryKVs) {
            int idx = queryKV.indexOf("=");
            if (idx == -1) {
                continue;
            }
            String key = queryKV.substring(0, idx);
            String value = queryKV.substring(idx + 1);
            queryMap.computeIfAbsent(key, (k) -> new ArrayList<>()).add(value);
        }
        return queryMap;
    }

}
