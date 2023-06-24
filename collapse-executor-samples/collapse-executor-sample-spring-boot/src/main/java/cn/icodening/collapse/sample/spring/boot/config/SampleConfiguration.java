package cn.icodening.collapse.sample.spring.boot.config;

import cn.icodening.collapse.spring.boot.http.reactive.CollapseExchangeFilterFunction;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author icodening
 * @date 2023.05.17
 */
@Configuration
public class SampleConfiguration {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public WebClient webClient(CollapseExchangeFilterFunction exchangeFilterFunction) {
        return WebClient.builder().filter(exchangeFilterFunction).build();
    }

    @Bean
    public ExecutorService executorService() {
        return new ThreadPoolExecutor(50, 100, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(), r -> {
            Thread thread = new Thread(r);
            thread.setDaemon(true);
            return thread;
        });
    }

}
