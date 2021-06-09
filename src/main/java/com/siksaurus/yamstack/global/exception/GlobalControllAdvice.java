package com.siksaurus.yamstack.global.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.NoSuchElementException;

@Slf4j
@ControllerAdvice
public class GlobalControllAdvice {

    @ExceptionHandler(LoginFailedException.class)
    protected ResponseEntity<ErrorResponse> loginFailedException (LoginFailedException e) {
        log.error("internal server exception", e);
        final ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.LOGIN_FAILED);
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(TokenValidFailedException.class)
    protected ResponseEntity<ErrorResponse> tokenValidFailedException (TokenValidFailedException e) {
        log.error("internal server exception", e);
        final ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.TOKEN_GENERATION_FAILED);
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(NoSuchElementException.class)
    protected ResponseEntity<ErrorResponse> noSuchElementException (NoSuchElementException e) {
        log.error("internal server exception", e);
        final ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.ENTITY_NOT_FOUND);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ErrorResponse> internalServerException (Exception e) {
        log.error("internal server exception", e);
        final ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.INTERNAL_SERVER_ERROR);
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
