package me.toyproject.mia.common;

import lombok.extern.slf4j.Slf4j;
import me.toyproject.mia.account.Account;
import me.toyproject.mia.service.AccountService;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AccountInitializingBean implements InitializingBean, DisposableBean {
    public static final String USER_EMAIL = "user@test.com";
    public static final String OTHER_USER_EMAIL = "other@test.com";

    @Autowired
    private AccountService accountService;
    @Override
    public void destroy() throws Exception {
        log.warn("destroy");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        log.warn("afterPropertiesSet");
        log.debug("accountService {}", accountService);
        Account account = accountService.create(USER_EMAIL, "NAME","password");
        Account otherAccount = accountService.create(OTHER_USER_EMAIL, "OTHER","password");
        log.debug("account {} {} ", account, otherAccount);
    }
}