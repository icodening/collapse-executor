package cn.icodening.collapse.sample.sequence;

import cn.icodening.collapse.sample.sequence.service.SequenceGeneratorSampleService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author icodening
 * @date 2023.07.14
 */
@SpringBootApplication
public class SequenceApplication {

    public static void main(String[] args) throws InterruptedException {
        ConfigurableApplicationContext context = SpringApplication.run(SequenceApplication.class);
        SequenceGeneratorSampleService sampleService = context.getBean(SequenceGeneratorSampleService.class);
        System.out.println("\n========================= get 'order' sequence===========================");
        sampleService.concurrentIncrement("order");

        Thread.sleep(300);
        System.out.println("\n========================= get 'product' sequence===========================");
        sampleService.concurrentIncrement("product");

        Thread.sleep(300);
        System.out.println("\n========================= get 'member' sequence [not exists]===========================");
        sampleService.concurrentIncrement("member");
    }
}
