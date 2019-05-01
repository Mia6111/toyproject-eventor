package me.toyproject.mia.config;

import lombok.extern.slf4j.Slf4j;
import me.toyproject.mia.persistence.AuthByAccount;
import me.toyproject.mia.persistence.AuthFinder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

import java.util.Optional;
@Slf4j
public class AccountEmailAuditorImpl implements AuditorAware<String> {
    @Autowired private AuthFinder authFinder;
    private static final String SYSTEM_USER = "SYSTEM";
    @Override
    public Optional<String> getCurrentAuditor() {
        AuthByAccount authByAccount = authFinder.getAuth();
        if(authByAccount == null || authByAccount.getAccount() == null){
            return Optional.of(SYSTEM_USER);
        }
        return Optional.of((authByAccount.getAccount()).getEmail());
    }
}
