package me.toyproejct.mia;

import lombok.Data;
import me.toyproejct.mia.domain.Account;

@Data
public class HostDto {
    private Long id;
    private String name;
    private String email;

    public Account toDomain() {
        Account account = new Account();
        account.setEmail(email);
        return account;
    }
}
