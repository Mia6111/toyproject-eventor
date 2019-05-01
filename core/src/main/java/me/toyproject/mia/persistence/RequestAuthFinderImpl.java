package me.toyproject.mia.persistence;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Objects;
@Component
public class RequestAuthFinderImpl implements ApplicationContextAware, AuthFinder {
    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        synchronized (this) {
            if (this.applicationContext == null) {
                this.applicationContext = applicationContext;
            }
        }
    }

    @Override
    public AuthByAccount getAuth() {
        try {
            AuthByAccount authByAccount = applicationContext.getBean(AuthByAccount.REQUEST_SCOPE_BEAN_KEY, AuthByAccount.class);
            if (Objects.isNull(authByAccount) || Objects.isNull(authByAccount.getAccount())) {
                return null;
            }
            return authByAccount;
        } catch (Exception e) {
            return null;
        }
    }
}
