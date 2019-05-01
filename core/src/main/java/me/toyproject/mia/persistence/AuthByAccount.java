package me.toyproject.mia.persistence;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.toyproject.mia.account.Account;
import org.apache.commons.lang3.ObjectUtils;

import javax.validation.constraints.NotNull;

@NoArgsConstructor
@Slf4j
public class AuthByAccount {

    public static final String HEADER_KEY = "AUTH-EMAIL";
    public static final String REQUEST_SCOPE_BEAN_KEY = "requestScopedApiAuth";

    private Account account;

    public void setAccount(Account account) {
        this.account = account;
        log.debug("account {}", this.account);
    }

    public @NotNull Account getAccount(){
        return ObjectUtils.defaultIfNull(this.account, Account.GUEST);
    }
}
