package me.toyproject.mia.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import me.toyproject.mia.domain.Account;
import org.springframework.stereotype.Service;

@Getter @Setter @NoArgsConstructor
public class HostDto {
    @JsonIgnore
    private Long id;
    private String name;
    private String email;

    public Account toDomain() {
        Account account = new Account();
        account.setEmail(email);
        return account;
    }

    public boolean isSameHost(HostDto hostDto) {
        return email != null && email.equals(hostDto.email);
    }
}
