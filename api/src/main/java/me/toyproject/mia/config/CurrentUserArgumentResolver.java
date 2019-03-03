package me.toyproject.mia.config;


import me.toyproject.mia.account.Account;
import me.toyproject.mia.account.AccountDetails;
import me.toyproject.mia.account.CurrentUser;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class CurrentUserArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        if(parameter.hasParameterAnnotation(CurrentUser.class) && parameter.getParameterType().isAssignableFrom(Account.class)){
            return true;
        }
        return false;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Object authPrincipal = auth.getPrincipal();
        if(authPrincipal == null || !authPrincipal.getClass().isAssignableFrom(AccountDetails.class)){
            return Account.GUEST;
        }
        return ((AccountDetails)authPrincipal).getAccount();
    }
}
