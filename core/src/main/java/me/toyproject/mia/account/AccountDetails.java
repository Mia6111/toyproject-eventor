package me.toyproject.mia.account;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

public class AccountDetails extends User {

    private Account account;

    public AccountDetails(Account account){
        super(account.getEmail(), account.getPassword(), authorities());
        this.account = account;
    }

    private static Collection<? extends GrantedAuthority> authorities() {
        return AuthorityUtils.createAuthorityList("ROLE_USER","ROLE_ADMIN");
    }

    public Account getAccount() {
        return account;
    }
}
