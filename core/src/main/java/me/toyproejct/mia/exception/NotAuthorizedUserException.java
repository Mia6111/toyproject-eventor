package me.toyproejct.mia.exception;

public class NotAuthorizedUserException extends RuntimeException {
    public NotAuthorizedUserException(String s) {
        super(s);
    }
}
