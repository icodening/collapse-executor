package cn.icodening.collapse.sample.spring.boot.controller;

import cn.icodening.collapse.sample.spring.boot.entity.UserEntity;
import cn.icodening.collapse.sample.spring.boot.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author icodening
 * @date 2023.05.17
 */
@RestController
@RequestMapping("/user")
public class UserController {

    public static final AtomicInteger SINGLE_GET_COUNTER = new AtomicInteger(0);

    public static final AtomicInteger BATCH_GET_COUNTER = new AtomicInteger(0);

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public UserEntity singleGetUser(@PathVariable Long id) {
        SINGLE_GET_COUNTER.incrementAndGet();
        return userService.getUser(id);
    }

    @GetMapping("/batchGet")
    public List<UserEntity> batchGetUser(@RequestParam(name = "id") Long[] ids) {
        BATCH_GET_COUNTER.incrementAndGet();
        return userService.batchGet(ids);
    }

}
