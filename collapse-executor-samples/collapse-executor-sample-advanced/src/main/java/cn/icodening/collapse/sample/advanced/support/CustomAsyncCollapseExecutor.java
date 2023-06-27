package cn.icodening.collapse.sample.advanced.support;

import cn.icodening.collapse.core.Bundle;
import cn.icodening.collapse.core.CollapseExecutorAsyncSupport;
import cn.icodening.collapse.core.Input;
import cn.icodening.collapse.core.LengthLimitedInputGrouper;
import cn.icodening.collapse.core.ListenableCollector;
import cn.icodening.collapse.core.NoOpInputGrouper;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

/**
 * @author icodening
 * @date 2023.06.27
 */
public class CustomAsyncCollapseExecutor extends CollapseExecutorAsyncSupport<Long, UserEntity, Map<Long, UserEntity>> {

    /**
     * mock remote service
     */
    private final UserService userService;

    public CustomAsyncCollapseExecutor(ListenableCollector collector, UserService userService) {
        super(collector);
        this.userService = userService;
        this.setBatchSize(Integer.MAX_VALUE);
    }

    public void setBatchSize(int size) {
        this.setInputGrouper(LengthLimitedInputGrouper.newInstance(size, NoOpInputGrouper.getInstance()));
    }

    public void setAsyncExecutor(ExecutorService executorService) {
        this.setExecutor(executorService);
    }

    @Override
    protected Map<Long, UserEntity> doExecute(Collection<Input<Long>> inputs) {
        //merge inputs and batch fetch
        List<Long> ids = inputs.stream().map(Input::value).collect(Collectors.toList());
        List<UserEntity> userList = userService.query(ids);
        System.out.println("[" + Thread.currentThread().getName() + "] async batch query ids:" + ids + "\n--------------------------------------------------------------------------------------------------");
        Map<Long, UserEntity> idUsers = new HashMap<>();
        for (UserEntity userEntity : userList) {
            if (userEntity == null) {
                continue;
            }
            Long id = userEntity.getId();
            idUsers.put(id, userEntity);
        }
        return idUsers;
    }

    @Override
    protected void bindingOutput(Map<Long, UserEntity> users, List<Bundle<Long, CompletableFuture<UserEntity>>> bundles) {
        for (Bundle<Long, CompletableFuture<UserEntity>> bundle : bundles) {
            Long inputId = bundle.getInput();
            UserEntity userEntity = users.get(inputId);
            bundle.bindOutput(CompletableFuture.completedFuture(userEntity));
        }
    }
}
