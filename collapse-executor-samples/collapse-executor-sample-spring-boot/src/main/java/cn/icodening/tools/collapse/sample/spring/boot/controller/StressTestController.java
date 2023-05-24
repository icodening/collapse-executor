package cn.icodening.tools.collapse.sample.spring.boot.controller;

import cn.icodening.tools.collapse.sample.spring.boot.entity.UserEntity;
import cn.icodening.tools.collapse.sample.spring.boot.service.UserService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author icodening
 * @date 2023.05.24
 */
@RequestMapping("/test")
@RestController
public class StressTestController {

    private final AtomicInteger collapseCounter = new AtomicInteger(0);

    private final AtomicInteger noOpCounter = new AtomicInteger(0);

    private final UserService userService;

    public StressTestController(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping("/collapse1")
    public UserEntity collapse1() throws InterruptedException {
        collapseCounter.incrementAndGet();
        Thread.sleep(1);
        return userService.getUser(1L);
    }

    @RequestMapping("/noop1")
    public UserEntity noop1() throws InterruptedException {
        noOpCounter.incrementAndGet();
        Thread.sleep(1);
        return userService.getUser(1L);
    }

    @RequestMapping("/collapse100")
    public UserEntity collapse100() throws InterruptedException {
        collapseCounter.incrementAndGet();
        Thread.sleep(100);
        return userService.getUser(1L);
    }

    @RequestMapping("/noop100")
    public UserEntity noop100() throws InterruptedException {
        noOpCounter.incrementAndGet();
        Thread.sleep(100);
        return userService.getUser(1L);
    }

    @RequestMapping("/counter")
    public Map<String, Integer> counter() {
        Map<String, Integer> result = new HashMap<>();
        result.put("collapse", collapseCounter.get());
        result.put("noop", noOpCounter.get());
        return result;
    }
}
