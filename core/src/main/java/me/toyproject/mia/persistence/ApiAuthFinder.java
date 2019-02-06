package me.toyproject.mia.persistence;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Objects;
@Component
public class ApiAuthFinder implements ApplicationContextAware {
    private static ApplicationContext applicationContext;

    private static final String SYSTEM_USER = "SYSTEM";

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        synchronized (this) {
            if (this.applicationContext == null) {
                this.applicationContext = applicationContext;
            }
        }
    }

    public static ApiAuth getRequestAuthInfo() {
        try {
            ApiAuth apiAuth = applicationContext.getBean(ApiAuth.REQUEST_SCOPE_BEAN_KEY, ApiAuth.class);
            if (Objects.isNull(apiAuth) || Objects.isNull(apiAuth.getAccount())) {
                return null;
            }
            return apiAuth;
        } catch (Exception e) {
            return null;
        }
    }
}
