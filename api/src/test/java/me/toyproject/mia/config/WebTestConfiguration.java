package me.toyproject.mia.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.toyproject.mia.account.AccountRepository;
import me.toyproject.mia.event.EventRepository;
import me.toyproject.mia.mock.ApiMockEntityHelper;
import me.toyproject.mia.persistence.AuthFinder;
import me.toyproject.mia.service.AccountService;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
    public ApiMockEntityHelper mockEntityHelper(){
        return new ApiMockEntityHelper(eventRepository, accountRepository);
    }
}
