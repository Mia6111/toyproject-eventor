package me.toyproject.mia.config;

import lombok.AllArgsConstructor;
import me.toyproject.mia.account.Account;
import me.toyproject.mia.persistence.ApiAuth;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
@AllArgsConstructor
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@Deprecated
public class ApiRequestInterceptor extends HandlerInterceptorAdapter {
    @Resource(name=ApiAuth.REQUEST_SCOPE_BEAN_KEY)
    private ApiAuth apiAuth;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        Account account = SessionUtils.getUserSession(request.getSession());
        apiAuth.setAccount(account);
        return true;
    }

}