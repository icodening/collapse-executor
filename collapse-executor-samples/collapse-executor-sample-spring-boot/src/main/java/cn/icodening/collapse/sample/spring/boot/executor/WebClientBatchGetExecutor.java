package cn.icodening.collapse.sample.spring.boot.executor;

import cn.icodening.collapse.core.Bundle;
import cn.icodening.collapse.core.CollapseExecutorBlockingSupport;
import cn.icodening.collapse.core.Input;
import cn.icodening.collapse.core.ListenableCollector;
import cn.icodening.collapse.core.NoOpInputGrouper;
import cn.icodening.collapse.sample.spring.boot.entity.UserEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author icodening
 * @date 2023.06.24
 */
@Component
public class WebClientBatchGetExecutor extends CollapseExecutorBlockingSupport<Long, UserEntity, List<UserEntity>> {

    private final WebClient webClient;

    private int serverPort;

    public WebClientBatchGetExecutor(ListenableCollector collector, WebClient webClient) {
        super(collector);
        this.webClient = webClient;
        this.setInputGrouper(NoOpInputGrouper.getInstance());
    }

    @Value("${server.port}")
    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }
    /**
     * batch request
     *
     * @param inputs ids
     * @return ids -> UserEntities
     */
    @Override
    protected List<UserEntity> doExecute(Collection<Input<Long>> inputs) throws Throwable {
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.addAll("id", new ArrayList<>(inputs.stream()
                .map(Input::value)
                .map(Objects::toString)
                .collect(Collectors.toSet())));
        //eg. http://localhost:8080/user/batchGet?id=1&id=2&id=3&id=4
        URI uri = UriComponentsBuilder.newInstance()
                .scheme("http")
                .host("localhost")
                .port(serverPort)
                .path("/user/batchGet")
                .queryParams(queryParams)
                .build()
                .toUri();
        ResponseEntity<List<UserEntity>> entity = this.webClient.get().uri(uri).retrieve().toEntity(new ParameterizedTypeReference<List<UserEntity>>() {
        }).block();
        Assert.notNull(entity, "'ResponseEntity' must be not null.");
        return entity.getBody();
    }

    /**
     * map response to inputs
     *
     * @param batchOutput batch response
     * @param bundles     input output bundle
     */
    @Override
    protected void bindingOutput(List<UserEntity> batchOutput, List<Bundle<Long, UserEntity>> bundles) {
        Map<Long, UserEntity> entityMap = new HashMap<>();
        for (UserEntity userEntity : batchOutput) {
            entityMap.put(userEntity.getId(), userEntity);
        }
        for (Bundle<Long, UserEntity> bundle : bundles) {
            Long id = bundle.getInput();
            UserEntity userEntity = entityMap.get(id);
            bundle.bindOutput(userEntity);
        }
    }
}
