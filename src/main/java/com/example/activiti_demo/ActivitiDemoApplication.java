package com.example.activiti_demo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author hebulin
 */
@SpringBootApplication
@Slf4j
public class ActivitiDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(ActivitiDemoApplication.class, args);
        log.info("=========ActivitiDemo启动完成=========");
    }
}
