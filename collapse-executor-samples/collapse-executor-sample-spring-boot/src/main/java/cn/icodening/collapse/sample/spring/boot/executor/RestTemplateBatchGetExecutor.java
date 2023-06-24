package cn.icodening.collapse.sample.spring.boot.executor;

import cn.icodening.collapse.core.ListenableCollector;
import cn.icodening.collapse.sample.spring.boot.entity.UserEntity;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.List;

/**
 * @author icodening
 * @date 2023.05.17
 */
@Component
public class RestTemplateBatchGetExecutor extends AbstractBatchGetExecutor {

    private final RestTemplate restTemplate;

    public RestTemplateBatchGetExecutor(ListenableCollector collector, RestTemplate restTemplate) {
        super(collector);
        this.restTemplate = restTemplate;
    }

    @Override
    protected List<UserEntity> doBatchGet(URI uri) {
        return restTemplate.exchange(uri, HttpMethod.GET, null, new ParameterizedTypeReference<List<UserEntity>>() {
        }).getBody();
    }
}
