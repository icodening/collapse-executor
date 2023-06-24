package cn.icodening.collapse.sample.spring.boot.service;

import cn.icodening.collapse.sample.spring.boot.entity.UserEntity;
import cn.icodening.collapse.sample.spring.boot.executor.RestTemplateBatchGetExecutor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * @author icodening
 * @date 2023.06.24
 */
@Component
public class RestTemplateCollapseBlockingCallSample extends AbstractBlockingCallSample {

    private final RestTemplate restTemplate;

    private final RestTemplateBatchGetExecutor restTemplateBatchGetExecutor;

    public RestTemplateCollapseBlockingCallSample(RestTemplate restTemplate,
                                                  RestTemplateBatchGetExecutor restTemplateBatchGetExecutor) {
        super(RestTemplate.class.getSimpleName());
        this.restTemplate = restTemplate;
        this.restTemplateBatchGetExecutor = restTemplateBatchGetExecutor;
    }

    @Override
    protected UserEntity doBatchQuery(long id) throws Throwable {
        return restTemplateBatchGetExecutor.execute(id);
    }

    @Override
    protected UserEntity doSingleQuery(UriComponentsBuilder baseUri, long id) {
        return restTemplate.getForObject(baseUri.path("/user/" + id).build().toUri(), UserEntity.class);
    }
}
