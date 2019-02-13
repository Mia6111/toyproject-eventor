package me.toyproject.mia.configuration;

import me.toyproject.mia.account.Account;

import javax.servlet.http.HttpSession;

public class SessionUtils {
    private static final String USER_SESSION_KEY = "loginUser";

    public static void setUserSession(HttpSession session, Account account) {
        session.setAttribute(USER_SESSION_KEY, account);
    }

    public static Account getUserSession(HttpSession session) {
        return (Account) session.getAttribute(USER_SESSION_KEY);
    }
}