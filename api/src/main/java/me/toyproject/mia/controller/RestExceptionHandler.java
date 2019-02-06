package me.toyproject.mia.controller;

import me.toyproject.mia.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(value = {DataNotFoundException.class})
    public ResponseEntity handleDataNotFoundException(Exception e){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }
    @ExceptionHandler(value = {NotAuthorizedUserException.class})
    public ResponseEntity handleNotAuthorizedUserException(Exception e){
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
    }
    @ExceptionHandler(value = {EventException.class, AccountCreateException.class, IllegalArgumentException.class})
    public ResponseEntity handleBadRequestException(Exception e){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
    @ExceptionHandler(value = {AccountLoginException.class})
    public ResponseEntity handleUnauthorizedException(Exception e){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

}
