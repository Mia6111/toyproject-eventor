package me.toyproejct.mia;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
public class CoreApplicatoin {
    public static void main(String[] args){
        SpringApplication.run(WebApplicationType.class, args);
    }
}
