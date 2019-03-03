package me.toyproject.mia.account;

import javax.persistence.Column;
import lombok.*;
import me.toyproject.mia.exception.AccountException;
import me.toyproject.mia.persistence.AuditingEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import java.util.Objects;

@Entity
@NoArgsConstructor @Getter @Setter @ToString
public class Account extends AuditingEntity {
    public static final Account GUEST = new Guest();
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Email @NotEmpty
    private String email;

    @NotEmpty
    @Column(name="account_name")
    private String name;

    @NotEmpty
    private String password;

    public Account(Long id, String email, String name, String password) {
        if(StringUtils.isEmpty(email) || StringUtils.isEmpty(name) || StringUtils.isEmpty(password)){
            throw new AccountException("email, name, password은 빈 값이 아니여야 합니다");
        }
        //이메일 정규식
        this.id = id;
        this.email = email;
        this.name = name;
        this.password = password;
    }
    @Builder
    public Account(Long id, String email, String name, String password, PasswordEncoder passwordEncoder) {
        if(StringUtils.isEmpty(email) || StringUtils.isEmpty(name) || StringUtils.isEmpty(password)){
            throw new AccountException("email, name, password은 빈 값이 아니여야 합니다");
        }
        //이메일 정규식
        this.id = id;
        this.email = email;
        this.name = name;
        this.password = passwordEncoder.encode(password);
    }

    public boolean isSameHost(Account host) {
        return host != null && Objects.equals(this.email, host.email);
    }

    public boolean matchPassword(String password, PasswordEncoder passwordEncoder) {
        return passwordEncoder.matches(this.password, password);
    }

    public boolean isGuest(){
        return false;
    }

    private static class Guest extends Account {
        @Override
        public boolean isGuest(){
            return true;
        }
    }
}
