package me.toyproject.mia.config;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.toyproject.mia.account.AccountRepository;
import me.toyproject.mia.event.EventRepository;
import me.toyproject.mia.mock.MockBuilder;
import me.toyproject.mia.mock.MockEntityHelper;
import me.toyproject.mia.service.AccountService;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.Ordered;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.test.context.ActiveProfilesResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
@Slf4j
@TestConfiguration
public class WebTestConfiguration implements WebMvcConfigurer {
    private final AccountService accountService;
    private final EventRepository eventRepository;
    private final AccountRepository accountRepository;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(new BasicAuthInterceptor(accountService)).order(Ordered.HIGHEST_PRECEDENCE);
    }

    @Bean
    public MockEntityHelper mockEntityHelper(){
        return new MockEntityHelper(eventRepository, accountRepository);
    }
}
