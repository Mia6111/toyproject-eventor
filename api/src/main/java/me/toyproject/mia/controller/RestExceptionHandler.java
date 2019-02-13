package me.toyproject.mia.controller;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import me.toyproject.mia.common.ErrorResponse;
import me.toyproject.mia.common.ValidationErrorMessage;
import me.toyproject.mia.exception.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class RestExceptionHandler {

    @ExceptionHandler(value = {DataNotFoundException.class})
    public ResponseEntity handleDataNotFoundException(Exception e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler(value = {NotAuthorizedUserException.class})
    public ResponseEntity handleNotAuthorizedUserException(Exception e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
    }

    @ExceptionHandler(value = {EventException.class, AccountCreateException.class, IllegalArgumentException.class})
    public ResponseEntity handleBadRequestException(Exception e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    @ExceptionHandler(value = {AccountLoginException.class})
    public ResponseEntity handleUnauthorizedException(Exception e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity handleConstraintViolationException(ConstraintViolationException ex) {
        try {
            List<String> messages = ex.getConstraintViolations().stream().map(ConstraintViolation::getMessage).collect(Collectors.toList());
            log.debug("handleConstraintViolationException {}", messages);
            return new ResponseEntity<>(messages, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(Lists.newArrayList(ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        List<ValidationErrorMessage> messages = ex.getBindingResult().getAllErrors().stream()
                .map(this::getErrorMessage)
                .collect(Collectors.toList());
        return new ResponseEntity<>(new ErrorResponse<>(messages), HttpStatus.BAD_REQUEST);

    }

    @Autowired
    private MessageSourceAccessor messageSourceAccessor;

    private ValidationErrorMessage getErrorMessage(ObjectError objectError) {
        FieldError fieldError = (FieldError) objectError;
        Optional<String> field = Optional.ofNullable(fieldError.getField());
        if (!field.isPresent()) {
            return ValidationErrorMessage.empty(objectError.getObjectName());
        }
        return new ValidationErrorMessage(fieldError.getObjectName(), fieldError.getField(),
                messageSourceAccessor.getMessage(fieldError));
    }
}
