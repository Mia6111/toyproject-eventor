package me.toyproject.mia.common;

import lombok.Getter;

@Getter
public class ErrorResponse<T>{
    private T errors;

    public ErrorResponse(T errors) {
        this.errors = errors;
    }
}

