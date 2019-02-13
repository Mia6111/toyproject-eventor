package me.toyproject.mia.account;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

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
