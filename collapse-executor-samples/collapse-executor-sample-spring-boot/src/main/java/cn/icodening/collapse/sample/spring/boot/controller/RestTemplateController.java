package cn.icodening.collapse.sample.spring.boot.controller;

import cn.icodening.collapse.sample.spring.boot.entity.UserEntity;
import cn.icodening.collapse.spring.boot.http.client.CollapseHttpRequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * @author icodening
 * @date 2023.06.30
 */
@RestController
@RequestMapping("/rest-template")
public class RestTemplateController {

    private final RestTemplate restTemplateWithCollapse;

    private final RestTemplate restTemplateWithoutCollapse;

    public RestTemplateController(CollapseHttpRequestInterceptor collapseHttpRequestInterceptor, @Value("${server.port}") int port) {
        UriComponentsBuilder baseUrl = UriComponentsBuilder.newInstance().scheme("http").host("localhost").port(port);
        this.restTemplateWithCollapse = new RestTemplateBuilder().rootUri(baseUrl.toUriString()).additionalInterceptors(collapseHttpRequestInterceptor).build();
        this.restTemplateWithoutCollapse = new RestTemplateBuilder().rootUri(baseUrl.toUriString()).build();
    }

    @RequestMapping(value = "/collapse/noop0")
    public UserEntity noop0WithCollapse() {
        return this.restTemplateWithCollapse.getForObject("/test/noop0", UserEntity.class);
    }

    @RequestMapping(value = "/collapse/noop1")
    public UserEntity noop1WithCollapse() {
        return this.restTemplateWithCollapse.getForObject("/test/noop1", UserEntity.class);
    }

    @RequestMapping(value = "/collapse/noop100")
    public UserEntity noop100WithCollapse() {
        return this.restTemplateWithCollapse.getForObject("/test/noop100", UserEntity.class);
    }

    @RequestMapping(value = "/without/noop0")
    public UserEntity noop0WithoutCollapse() {
        return this.restTemplateWithoutCollapse.getForObject("/test/noop0", UserEntity.class);
    }

    @RequestMapping(value = "/without/noop1")
    public UserEntity noop1WithoutCollapse() {
        return this.restTemplateWithoutCollapse.getForObject("/test/noop1", UserEntity.class);
    }

    @RequestMapping(value = "/without/noop100")
    public UserEntity noop100WithoutCollapse() {
        return this.restTemplateWithoutCollapse.getForObject("/test/noop100", UserEntity.class);
    }
}
