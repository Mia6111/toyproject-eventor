package me.toyproject.mia.persistence;

import me.toyproject.mia.account.Account;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.validation.constraints.NotNull;

public class TestAuthFinderImpl implements AuthFinder {
    private static final Account TEST_ACCOUNT = Account.builder().email("abcdef@aa.com").name("테스트야").password("PASS").mobile("01012341234").passwordEncoder(new BCryptPasswordEncoder()).build();

    @Override
    public @NotNull AuthByAccount getAuth() {
        AuthByAccount authByAccount = new AuthByAccount();
        authByAccount.setAccount(TEST_ACCOUNT);
        return authByAccount;
    }

    public Account getTestAccount() {
        return TEST_ACCOUNT;
    }
}
