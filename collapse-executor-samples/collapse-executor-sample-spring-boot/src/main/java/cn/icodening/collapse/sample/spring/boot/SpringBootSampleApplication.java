package cn.icodening.collapse.sample.spring.boot;

import cn.icodening.collapse.sample.spring.boot.controller.UserController;
import cn.icodening.collapse.sample.spring.boot.entity.UserEntity;
import cn.icodening.collapse.sample.spring.boot.executor.BatchGetExecutor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.concurrent.ExecutorService;

/**
 * @author icodening
 * @date 2023.05.17
 */
@SpringBootApplication
public class SpringBootSampleApplication {

    public static void main(String[] args) throws Throwable {
        ConfigurableApplicationContext context = SpringApplication.run(SpringBootSampleApplication.class);
        ServerProperties serverProperties = context.getBean(ServerProperties.class);
        Integer port = serverProperties.getPort();
        RestTemplate restTemplate = context.getBean(RestTemplate.class);
        ExecutorService executorService = context.getBean("executorService", ExecutorService.class);
        BatchGetExecutor batchGetExecutor = context.getBean(BatchGetExecutor.class);
        UriComponentsBuilder baseUriBuilder = UriComponentsBuilder.fromUriString("http://localhost").port(port);

        queryId1between50(executorService, restTemplate, baseUriBuilder);
        queryId1between50Batch(executorService, batchGetExecutor);
        queryId1between2(executorService, restTemplate, baseUriBuilder);
    }

    /**
     * query id [1,50] in 50 times
     */
    private static void queryId1between50(ExecutorService executorService, RestTemplate restTemplate, UriComponentsBuilder baseUri) throws InterruptedException {
        System.out.println("----------------------------------query id [1,50]----------------------------------");
        for (int i = 1; i <= 50; i++) {
            final long id = i;
            executorService.submit(() -> {
                UserEntity userEntity = singleQuery(restTemplate, baseUri.cloneBuilder(), id);
                System.out.println("query id:" + id + ", " + userEntity);
            });
        }
        Thread.sleep(500);
        System.err.println("/user/{id} execute times:" + UserController.SINGLE_GET_COUNTER.intValue());//expected result equals 50
        UserController.SINGLE_GET_COUNTER.set(0);
    }

    private static void queryId1between50Batch(ExecutorService executorService, BatchGetExecutor batchGetExecutor) throws InterruptedException {
        System.out.println("----------------------------------batch query id [1,50]----------------------------------");
        for (int i = 1; i <= 50; i++) {
            final long id = i;
            executorService.submit(() -> {
                try {
                    UserEntity userEntity = batchGetExecutor.execute(id);
                    System.out.println("query id:" + id + ", " + userEntity);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            });
        }
        Thread.sleep(500);
        System.err.println("/user/batchGet execute times:" + UserController.BATCH_GET_COUNTER.intValue());//expected result less than 50
        UserController.BATCH_GET_COUNTER.set(0);
    }

    /**
     * query id [1,2] in 50 times
     */
    private static void queryId1between2(ExecutorService executorService, RestTemplate restTemplate, UriComponentsBuilder baseUri) throws InterruptedException {
        System.out.println("----------------------------------query id [1,2]----------------------------------");
        for (int i = 1; i <= 50; i++) {
            final long id = i % 2 + 1;
            executorService.submit(() -> {
                UserEntity userEntity = singleQuery(restTemplate, baseUri.cloneBuilder(), id);
                System.out.println("query id:" + id + ", " + userEntity);
            });
        }
        Thread.sleep(500);
        System.err.println("/user/{id} execute times:" + UserController.SINGLE_GET_COUNTER.intValue());//expected result less than 50
        UserController.SINGLE_GET_COUNTER.set(0);
    }

    private static UserEntity singleQuery(RestTemplate restTemplate, UriComponentsBuilder baseUri, Long id) {
        return restTemplate.getForObject(baseUri.path("/user/" + id).build().toUri(), UserEntity.class);
    }

}
