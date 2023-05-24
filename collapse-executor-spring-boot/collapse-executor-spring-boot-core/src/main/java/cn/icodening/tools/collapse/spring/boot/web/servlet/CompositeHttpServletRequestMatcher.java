package cn.icodening.tools.collapse.spring.boot.web.servlet;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * @author icodening
 * @date 2023.05.23
 */
public class CompositeHttpServletRequestMatcher implements HttpServletRequestMatcher {

    private Collection<HttpServletRequestMatcher> matchers = new ArrayList<>();

    public CompositeHttpServletRequestMatcher() {
    }

    public CompositeHttpServletRequestMatcher(Collection<HttpServletRequestMatcher> matchers) {
        this.matchers = new ArrayList<>(matchers);
    }

    public void setMatchers(Collection<HttpServletRequestMatcher> matchers) {
        this.matchers = matchers == null ? new ArrayList<>() : new ArrayList<>(matchers);
    }

    public void addMatcher(HttpServletRequestMatcher... matchers) {
        this.matchers.addAll(Arrays.asList(matchers));
    }

    @Override
    public boolean match(HttpServletRequest httpServletRequest) {
        for (HttpServletRequestMatcher matcher : this.matchers) {
            boolean match = matcher.match(httpServletRequest);
            if (match) {
                return true;
            }
        }
        return false;
    }
}
