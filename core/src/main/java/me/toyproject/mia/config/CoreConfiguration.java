package me.toyproject.mia.config;

import me.toyproject.mia.persistence.AuthFinder;
import me.toyproject.mia.persistence.SecurityAuthFinderImpl;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

@Configuration
public class CoreConfiguration {
    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:messages");
        messageSource.setDefaultEncoding("UTF-8");

        return messageSource;
    }
    @Bean
    public MessageSourceAccessor messageSourceAccessor(){
       return new MessageSourceAccessor(messageSource());
    }

    @Bean
    @Profile("!test")
    public AuthFinder authFinder(){
        return new SecurityAuthFinderImpl();
    }
}
