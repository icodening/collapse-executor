package cn.icodening.tools.collapse.sample.spring.boot.executor;

import cn.icodening.tools.collapse.core.AbstractCollapseExecutor;
import cn.icodening.tools.collapse.core.Bundle;
import cn.icodening.tools.collapse.core.Input;
import cn.icodening.tools.collapse.core.ListeningBundleCollector;
import cn.icodening.tools.collapse.core.NoOpInputGrouper;
import cn.icodening.tools.collapse.sample.spring.boot.entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author icodening
 * @date 2023.05.17
 */
@Component
public class BatchGetExecutor extends AbstractCollapseExecutor<Long, UserEntity, List<UserEntity>> {

    private final RestTemplate restTemplate;

    private ServerProperties serverProperties;

    public BatchGetExecutor(ListeningBundleCollector collector, RestTemplate restTemplate) {
        super(collector);
        this.restTemplate = restTemplate;
        this.setInputGrouper(NoOpInputGrouper.getInstance());
    }

    @Autowired
    public void setServerProperties(ServerProperties serverProperties) {
        this.serverProperties = serverProperties;
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
                .port(serverProperties.getPort())
                .path("/user/batchGet")
                .queryParams(queryParams)
                .build()
                .toUri();
        return restTemplate.exchange(uri, HttpMethod.GET, null, new ParameterizedTypeReference<List<UserEntity>>() {
        }).getBody();
    }

    /**
     * map response to inputs
     *
     * @param batchOutput batch response
     * @param bundles     input output bundle
     */
    @Override
    protected void bindingOutput(List<UserEntity> batchOutput, List<Bundle<Long, UserEntity>> bundles) {
        Map<Long, List<Bundle<Long, UserEntity>>> idBundles = bundles.stream().collect(Collectors.groupingBy(Bundle::getInput));
        for (UserEntity userEntity : batchOutput) {
            List<Bundle<Long, UserEntity>> idBundleGroup = idBundles.get(userEntity.getId());
            for (Bundle<Long, UserEntity> bundle : idBundleGroup) {
                bundle.bindOutput(userEntity);
            }
        }
    }
}