package me.toyproject.mia.configuration;

import lombok.AllArgsConstructor;
import me.toyproject.mia.service.AccountService;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@AllArgsConstructor
@Import(ApiAuthConfiguration.class)
public class WebTestConfiguration implements WebMvcConfigurer {
    private AccountService accountService;
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new BasicAuthInterceptor(accountService)).order(Ordered.HIGHEST_PRECEDENCE);
    }
}
