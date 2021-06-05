package com.siksaurus.yamstack.global.exception;

public class AccountAuthenticationException extends RuntimeException{

    public AccountAuthenticationException() {
        super(ErrorCode.AUTHENTICATION_FAILED.getMessage());
    }

    public AccountAuthenticationException(Exception ex) {
        super(ex);
    }
}
