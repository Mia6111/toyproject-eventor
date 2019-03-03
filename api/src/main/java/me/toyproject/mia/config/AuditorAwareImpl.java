package me.toyproject.mia.config;

import me.toyproject.mia.persistence.ApiAuth;
import org.springframework.data.domain.AuditorAware;

import javax.annotation.Resource;
import java.util.Optional;
@Deprecated
public class AuditorAwareImpl implements AuditorAware<String> {
    private final static String SYSTEM_USER = "SYSTEM";

    @Resource(name= ApiAuth.REQUEST_SCOPE_BEAN_KEY)
    private ApiAuth apiAuth;

    @Override
    public Optional<String> getCurrentAuditor() {
        if(apiAuth == null || apiAuth.getAccount() == null || apiAuth.getAccount().getEmail() == null){
            return Optional.of(SYSTEM_USER);
        }
        return Optional.ofNullable(apiAuth.getAccount().getEmail());
    }
}
