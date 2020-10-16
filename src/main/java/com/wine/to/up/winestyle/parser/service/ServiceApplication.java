package com.wine.to.up.winestyle.parser.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@ComponentScan({"com.wine.to.up", "com.wine.to.up.winestyle.parser.service.service"})
@EntityScan("com.wine.to.up.winestyle.parser.service.domain")
@EnableJpaRepositories("com.wine.to.up.winestyle.parser.service.repository")
@EnableSwagger2
@EnableScheduling
public class ServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceApplication.class, args);
    }
}
