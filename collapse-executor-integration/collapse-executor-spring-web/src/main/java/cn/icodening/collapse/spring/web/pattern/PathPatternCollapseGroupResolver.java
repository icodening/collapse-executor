package cn.icodening.collapse.spring.web.pattern;

import org.springframework.http.server.PathContainer;
import org.springframework.web.util.pattern.PathPatternParser;

/**
 * @author icodening
 * @date 2023.07.15
 * @see PathPatternParser
 */
public class PathPatternCollapseGroupResolver extends ConfigurationCollapseGroupResolver {

    private static final PathPatternParser PATTERN_PARSER = PathPatternParser.defaultInstance;

    public PathPatternCollapseGroupResolver(CollapseDefinitionProperties collapseDefinitionProperties) {
        super(collapseDefinitionProperties);
    }

    @Override
    protected boolean matches(RequestAttributes request, String pattern) {
        String path = request.getURI().getPath();
        return PATTERN_PARSER.parse(pattern)
                .matches(PathContainer.parsePath(path));
    }
}
