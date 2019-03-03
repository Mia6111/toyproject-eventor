package me.toyproject.mia.controller;

import lombok.AllArgsConstructor;
import me.toyproject.mia.config.SessionUtils;
import me.toyproject.mia.account.Account;
import me.toyproject.mia.service.AccountService;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/accounts")
@AllArgsConstructor
public class AccountController {
    private AccountService accountService;

    @PostMapping("/login")
    public void login(@RequestBody Map<String, String> requestBodyMap, HttpSession httpSession){
        Assert.notNull(requestBodyMap.get("email"), "email은 필수값입니다");
        Assert.notNull(requestBodyMap.get("password"), "password는 필수값입니다");

        final Account account = accountService.authenticate(requestBodyMap.get("email"), requestBodyMap.get("password"));
        SessionUtils.setUserSession(httpSession, account);
    }

}
