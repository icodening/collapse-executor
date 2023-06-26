package cn.icodening.collapse.sample.advanced.support;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author icodening
 * @date 2023.06.27
 */
public class UserService {

    private final Map<Long, UserEntity> users = new ConcurrentHashMap<>();

    public UserService() {
        initialize();
    }

    public void initialize() {
        for (long i = 1; i <= 10; i++) {
            users.put(i, new UserEntity(i, UUID.randomUUID().toString()));
        }
    }

    public UserEntity getOne(Long id) {
        if (id == null) {
            return null;
        }
        return users.get(id);
    }

    public List<UserEntity> query(List<Long> ids) {
        if (ids == null) {
            return Collections.emptyList();
        }
        return ids.stream().map(users::get).collect(Collectors.toList());
    }
}
