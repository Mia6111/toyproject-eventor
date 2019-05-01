package me.toyproject.mia.config;

import me.toyproject.mia.persistence.AuthFinder;
import me.toyproject.mia.persistence.TestAuthFinderImpl;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

@TestConfiguration
public class CoreTestConfiguration {

    @Bean
    @Profile("test")
    public AuthFinder authFinder(){
        return new TestAuthFinderImpl();
    }
}
