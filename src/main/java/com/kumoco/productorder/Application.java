package com.kumoco.productorder;

import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.jms.annotation.EnableJms;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * productorder API application class
 */
@SpringBootApplication
@EnableSwagger2
@EnableJms
@Log4j2
@ComponentScan(basePackages = { "com.kumoco.productorder", "com.kumoco.productorder.api" })
public class Application {

    public static void main(String[] args) {

        SpringApplication.run(Application.class, args);
        log.info("Springboot with profiles application is running successfully.");
    }
}


