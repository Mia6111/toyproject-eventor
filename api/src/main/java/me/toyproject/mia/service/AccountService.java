package me.toyproject.mia.service;

import lombok.AllArgsConstructor;
import ma.glasnost.orika.MapperFacade;
import me.toyproject.mia.account.Account;
import me.toyproject.mia.account.AccountDetails;
import me.toyproject.mia.account.AccountRepository;
import me.toyproject.mia.account.HostDto;
import me.toyproject.mia.exception.AccountCreateException;
import me.toyproject.mia.exception.AccountLoginException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import org.springframework.util.Assert;

@Service
@AllArgsConstructor
public class AccountService implements UserDetailsService {
    private AccountRepository accountRepository;
    private PasswordEncoder passwordEncoder;
    private MapperFacade orikaMapper;

    public HostDto findHostById(Long id) {
        Assert.notNull(id, "id must not be null");
        Account account = accountRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        return orikaMapper.map(account, HostDto.class);
    }

    protected Account findById(Long id) {
        Assert.notNull(id, "id must not be null");
        Account account = accountRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        return account;
    }
    public Account create(String email, String name, String password) {
        accountRepository.findByEmail(email).ifPresent((account) -> {
            throw new AccountCreateException("중복 이메일이 존재 -" + account.getEmail());
        });

        Account account = Account.builder()
                .email(email)
                .name(name)
                .password(password)
                .passwordEncoder(passwordEncoder)
                .mobile("01012341234")
                .build();

        return accountRepository.save(account);
    }

    public Account authenticate(String email, String password) {
        Account account = accountRepository.findByEmail(email).orElseThrow(EntityNotFoundException::new);
        if (account.matchPassword(password, passwordEncoder)) {
            throw new AccountLoginException("패스워드 불일치");
        }
        return account;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return new AccountDetails(accountRepository.findByEmail(username).orElseThrow(EntityNotFoundException::new));
    }


}
