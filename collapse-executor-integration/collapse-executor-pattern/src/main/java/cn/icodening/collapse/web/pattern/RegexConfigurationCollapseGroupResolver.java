package cn.icodening.collapse.web.pattern;

import java.net.URI;
import java.util.regex.Pattern;

/**
 * @author icodening
 * @date 2023.07.16
 */
public class RegexConfigurationCollapseGroupResolver extends AbstractConfigurationCollapseGroupResolver {

    public RegexConfigurationCollapseGroupResolver(CollapseDefinitionProperties collapseDefinitionProperties) {
        super(collapseDefinitionProperties);
    }

    @Override
    protected boolean matches(RequestAttributes request, String pattern) {
        URI uri = request.getURI();
        String path = uri.getPath();
        return Pattern.matches(pattern, path);
    }
}
