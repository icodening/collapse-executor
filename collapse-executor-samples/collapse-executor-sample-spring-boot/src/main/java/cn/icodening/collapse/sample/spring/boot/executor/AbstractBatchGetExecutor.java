package cn.icodening.collapse.sample.spring.boot.executor;

import cn.icodening.collapse.core.Bundle;
import cn.icodening.collapse.core.CollapseExecutorBlockingSupport;
import cn.icodening.collapse.core.Input;
import cn.icodening.collapse.core.ListeningCollector;
import cn.icodening.collapse.core.NoOpInputGrouper;
import cn.icodening.collapse.sample.spring.boot.entity.UserEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
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
 * @date 2023.06.25
 */
public abstract class AbstractBatchGetExecutor extends CollapseExecutorBlockingSupport<Long, UserEntity, List<UserEntity>> {

    private int serverPort;

    @Value("${server.port}")
    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    public AbstractBatchGetExecutor(ListeningCollector collector) {
        super(collector);
        this.setInputGrouper(NoOpInputGrouper.getInstance());
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
        return doBatchGet(uri);
    }

    protected abstract List<UserEntity> doBatchGet(URI uri);


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
            if (userEntity == null) {
                bundle.bindOutput(null);
                continue;
            }
            //copy a response
            UserEntity duplicate = new UserEntity();
            duplicate.setId(userEntity.getId());
            duplicate.setUsername(userEntity.getUsername());
            duplicate.setPassword(userEntity.getPassword());
            bundle.bindOutput(duplicate);
        }
    }
}
