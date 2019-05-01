package me.toyproject.mia.persistence;

import me.toyproject.mia.account.Account;
import me.toyproject.mia.account.AccountDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

@Component
public class SecurityAuthFinderImpl implements AuthFinder {

    @Override
    public AuthByAccount getAuth() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        AuthByAccount authByAccount = new AuthByAccount();
        User user = (User) authentication.getPrincipal();
        assignAccountFromUser(authByAccount, user);

        return authByAccount;
    }
    private void assignAccountFromUser(AuthByAccount authByAccount, User user) {
        if(user.getClass().isAssignableFrom(AccountDetails.class)){
            authByAccount.setAccount(AccountDetails.class.cast(user).getAccount());
        }
        authByAccount.setAccount(Account.GUEST);
    }
}
