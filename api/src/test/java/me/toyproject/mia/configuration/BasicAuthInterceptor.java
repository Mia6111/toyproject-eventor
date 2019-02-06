package me.toyproject.mia.configuration;

import lombok.AllArgsConstructor;
import me.toyproject.mia.domain.Account;
import me.toyproject.mia.service.AccountService;
import org.apache.logging.log4j.util.Strings;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.http.HttpHeaders;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.nio.charset.StandardCharsets;

@AllArgsConstructor
public class BasicAuthInterceptor implements HandlerInterceptor {
    private static final String BASIC_AUTH_HEADER = "Basic";
    private static final String BASIC_AUTH_SPLITTER = ":";

    private AccountService accountService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.isEmpty(authorization) || !authorization.startsWith(BASIC_AUTH_HEADER)) {
            return true;
        }
        String encodedCredentials = authorization.replaceAll(BASIC_AUTH_HEADER, Strings.EMPTY);
        String credentials = new String(Base64.decodeBase64(encodedCredentials), StandardCharsets.UTF_8);
        String[] values = credentials.split(BASIC_AUTH_SPLITTER);
        Account account = accountService.authenticate(values[0], values[1]);

        HttpSession session = request.getSession();
        SessionUtils.setUserSession(session, account);

        return true;
    }
}
