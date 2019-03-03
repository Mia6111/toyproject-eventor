package me.toyproject.mia.config;

import lombok.extern.slf4j.Slf4j;
import me.toyproject.mia.account.Account;
import me.toyproject.mia.account.AccountDetails;
import me.toyproject.mia.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.AuthenticationEntryPoint;
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Slf4j
public class ApiWebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Value("${spring.csrf-enabled:true}")
    private boolean csrfEnabled;

    @Autowired
    private AccountService accountService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .formLogin()
                .loginPage("/login").permitAll()
                .defaultSuccessUrl("/")
                .failureUrl("/error")
                .and()
                .logout()
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(restAuthenticationEntryPoint())
                .and()
                .authorizeRequests()
                .mvcMatchers(HttpMethod.POST,"api/v1/**").hasAnyRole("USER")
                .mvcMatchers(HttpMethod.PUT,"api/v1/**").hasAnyRole("USER")
                .mvcMatchers(HttpMethod.DELETE,"api/v1/**").hasAnyRole("USER")
                .and()
                .authorizeRequests()
                .mvcMatchers(HttpMethod.GET,"/api/v1/events/**").permitAll();

        log.debug("!csrfEnabled {}",!csrfEnabled );
        if(!csrfEnabled) {
            http.csrf().disable();
        }
    }
    @Bean
    public AuthenticationEntryPoint restAuthenticationEntryPoint() {
        return new RestAuthenticationEntryPoint();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(accountService);
        // auth.userDetailsService(inMemoryUserDetailService());
    }
/*
    @Bean
    public UserDetailsService inMemoryUserDetailService() throws Exception {
        // ensure the passwords are encoded properly
        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
        manager.createUser(new AccountDetails(new Account(-1L, "user1@test.com","testUser", "password")));
        return manager;
    }
*/
    public void configure(WebSecurity web) throws Exception {
        super.configure(web);
    }
}
