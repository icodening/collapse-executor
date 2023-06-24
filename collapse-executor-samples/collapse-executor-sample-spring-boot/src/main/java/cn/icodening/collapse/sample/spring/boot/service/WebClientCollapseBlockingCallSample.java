package cn.icodening.collapse.sample.spring.boot.service;

import cn.icodening.collapse.sample.spring.boot.entity.UserEntity;
import cn.icodening.collapse.sample.spring.boot.executor.WebClientBatchGetExecutor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * @author icodening
 * @date 2023.06.24
 */
@Component
public class WebClientCollapseBlockingCallSample extends AbstractBlockingCallSample {

    private final WebClient webClient;

    private final WebClientBatchGetExecutor webClientBatchGetExecutor;

    public WebClientCollapseBlockingCallSample(WebClient webClient,
                                               WebClientBatchGetExecutor webClientBatchGetExecutor) {
        super(WebClient.class.getSimpleName());
        this.webClient = webClient;
        this.webClientBatchGetExecutor = webClientBatchGetExecutor;
    }

    @Override
    protected UserEntity doBatchQuery(long id) throws Throwable {
        return webClientBatchGetExecutor.execute(id);
    }

    @Override
    protected UserEntity doSingleQuery(UriComponentsBuilder baseUri, long id) {
        return webClient.get()
                .uri(baseUri.path("/user/" + id).build().toUri())
                .retrieve()
                .bodyToMono(UserEntity.class)
                .block();
    }
}
