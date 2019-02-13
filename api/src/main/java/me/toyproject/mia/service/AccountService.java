package me.toyproject.mia.service;

import lombok.AllArgsConstructor;
import ma.glasnost.orika.MapperFacade;
import me.toyproject.mia.account.Account;
import me.toyproject.mia.account.AccountRepository;
import me.toyproject.mia.account.HostDto;
import me.toyproject.mia.exception.AccountCreateException;
import me.toyproject.mia.exception.AccountLoginException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;

@Service
@AllArgsConstructor
public class AccountService {
    private AccountRepository accountRepository;
    private PasswordEncoder passwordEncoder;
    private MapperFacade orikaMapper;

    public HostDto findHostById(Long id) {
        Account account = accountRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        return orikaMapper.map(account, HostDto.class);
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


}
