package me.toyproject.mia.persistence;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.toyproject.mia.domain.Account;

@Getter
@NoArgsConstructor
@Slf4j
public class ApiAuth {

    public static final String HEADER_KEY = "AUTH-EMAIL";
    public static final String REQUEST_SCOPE_BEAN_KEY = "requestScopedApiAuth";

    private Account account;

    public void setAccount(Account account) {
        this.account = account;
        log.debug("account {}", this.account);
    }
}
