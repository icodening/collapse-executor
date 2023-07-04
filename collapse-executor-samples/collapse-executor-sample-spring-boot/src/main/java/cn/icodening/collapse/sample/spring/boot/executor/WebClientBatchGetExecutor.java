package cn.icodening.collapse.sample.spring.boot.executor;

import cn.icodening.collapse.core.ListeningCollector;
import cn.icodening.collapse.sample.spring.boot.entity.UserEntity;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URI;
import java.util.List;

/**
 * @author icodening
 * @date 2023.06.24
 */
@Component
public class WebClientBatchGetExecutor extends AbstractBatchGetExecutor {

    private final WebClient webClient;

    public WebClientBatchGetExecutor(ListeningCollector collector) {
        super(collector);
        this.webClient = WebClient.create();
    }

    @Override
    protected List<UserEntity> doBatchGet(URI uri) {
        ResponseEntity<List<UserEntity>> entity = this.webClient.get().uri(uri).retrieve().toEntity(new ParameterizedTypeReference<List<UserEntity>>() {
        }).block();
        Assert.notNull(entity, "'ResponseEntity' must be not null.");
        return entity.getBody();
    }
}
