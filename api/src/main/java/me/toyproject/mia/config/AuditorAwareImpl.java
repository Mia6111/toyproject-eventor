package me.toyproject.mia.config;

import me.toyproject.mia.persistence.AuthByAccount;
import org.springframework.data.domain.AuditorAware;

import javax.annotation.Resource;
import java.util.Optional;
@Deprecated
public class AuditorAwareImpl implements AuditorAware<String> {
    private final static String SYSTEM_USER = "SYSTEM";

    @Resource(name= AuthByAccount.REQUEST_SCOPE_BEAN_KEY)
    private AuthByAccount authByAccount;

    @Override
    public Optional<String> getCurrentAuditor() {
        if(authByAccount == null || authByAccount.getAccount() == null || authByAccount.getAccount().getEmail() == null){
            return Optional.of(SYSTEM_USER);
        }
        return Optional.ofNullable(authByAccount.getAccount().getEmail());
    }
}
