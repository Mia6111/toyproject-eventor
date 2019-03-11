package me.toyproject.mia.config;

import java.util.HashMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import me.toyproject.mia.account.UserNotificationMethod;
import me.toyproject.mia.infra.NotificationEngine;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@AllArgsConstructor
@EnableJpaAuditing(auditorAwareRef="auditorProvider")
public class ApiWebMvcConfig implements WebMvcConfigurer {
    @Bean
    public AuditorAware<String> auditorProvider() {
        return new AccountEmailAuditorImpl();
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new CurrentUserArgumentResolver());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public Map<UserNotificationMethod, NotificationEngine> notificationEngineMap(){
        Map notificationEngineMap = new HashMap<UserNotificationMethod, NotificationEngine>();
        notificationEngineMap.put(UserNotificationMethod.MOBILE_MSG, new NotificationEngine());
        notificationEngineMap.put(UserNotificationMethod.EMAIL, new NotificationEngine());
        return notificationEngineMap;

    }


}
