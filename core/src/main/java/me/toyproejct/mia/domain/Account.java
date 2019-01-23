package me.toyproejct.mia.domain;

import jdk.nashorn.internal.runtime.regexp.RegExpMatcher;
import lombok.*;
import org.springframework.util.StringUtils;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Entity
@NoArgsConstructor @Getter @Setter(AccessLevel.PRIVATE)
public class Account extends AuditingEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Email @NotEmpty
    private String email;

    @NotEmpty
    private String name;


    @Builder
    public Account(String email, String name) {
        if(StringUtils.isEmpty(email) || StringUtils.isEmpty(name)){
            throw new IllegalArgumentException("email과 name은 빈 값이 아니여야 합니다");
        }
        //이메일 정규식

        this.email = email;
        this.name = name;
    }
}
