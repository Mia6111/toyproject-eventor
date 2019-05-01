package me.toyproject.mia.config;

import lombok.AllArgsConstructor;
import me.toyproject.mia.persistence.AuthByAccount;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@AllArgsConstructor
@Deprecated
public class ApiAuthConfiguration implements WebMvcConfigurer {

    private ApiRequestInterceptor apiRequestInterceptor;

    @Bean(AuthByAccount.REQUEST_SCOPE_BEAN_KEY)
    @Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
    public AuthByAccount requestScopedApiAuth() {
        return new AuthByAccount();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(apiRequestInterceptor).excludePathPatterns("/api/v1/accounts/login");
    }
}
