package cn.icodening.collapse.sample.spring.boot.controller;

import cn.icodening.collapse.sample.spring.boot.entity.UserEntity;
import cn.icodening.collapse.spring.boot.http.reactive.CollapseExchangeFilterFunction;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

/**
 * @author icodening
 * @date 2023.06.30
 */
@RestController
@RequestMapping("/webclient")
public class WebClientController {

    private final WebClient webClientWithCollapse;

    private final WebClient webClientWithoutCollapse;

    public WebClientController(CollapseExchangeFilterFunction exchangeFilterFunction,
                               @Value("${server.port}") int port) {
        UriComponentsBuilder baseUrl = UriComponentsBuilder.newInstance()
                .scheme("http")
                .host("localhost")
                .port(port);
        DefaultUriBuilderFactory defaultUriBuilderFactory = new DefaultUriBuilderFactory(baseUrl);
        this.webClientWithCollapse = WebClient.builder().uriBuilderFactory(defaultUriBuilderFactory).filter(exchangeFilterFunction).build();
        this.webClientWithoutCollapse = WebClient.builder().uriBuilderFactory(defaultUriBuilderFactory).build();
    }

    @RequestMapping(value = "/collapse/noop0")
    public Mono<UserEntity> noop0WithCollapse() {
        return this.webClientWithCollapse.get()
                .uri("/test/noop0")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(UserEntity.class);
    }

    @RequestMapping(value = "/collapse/noop1")
    public Mono<UserEntity> noop1WithCollapse() {
        return this.webClientWithCollapse.get()
                .uri("/test/noop1")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(UserEntity.class);
    }

    @RequestMapping(value = "/collapse/noop100")
    public Mono<UserEntity> noop100WithCollapse() {
        return this.webClientWithCollapse.get()
                .uri("/test/noop100")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(UserEntity.class);
    }

    @RequestMapping(value = "/without/noop0")
    public Mono<UserEntity> noop0WithoutCollapse() {
        return this.webClientWithoutCollapse.get()
                .uri("/test/noop0")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(UserEntity.class);
    }

    @RequestMapping(value = "/without/noop1")
    public Mono<UserEntity> noop1WithoutCollapse() {
        return this.webClientWithoutCollapse.get()
                .uri("/test/noop1")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(UserEntity.class);
    }

    @RequestMapping(value = "/without/noop100")
    public Mono<UserEntity> noop100WithoutCollapse() {
        return this.webClientWithoutCollapse.get()
                .uri("/test/noop100")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(UserEntity.class);
    }
}
