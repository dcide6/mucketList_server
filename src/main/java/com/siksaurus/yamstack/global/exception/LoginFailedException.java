package com.siksaurus.yamstack.global.exception;

public class LoginFailedException extends RuntimeException {

    public LoginFailedException(){
        super(ErrorCode.Login_FAILED.getMessage());
    }

    private LoginFailedException(String msg){
        super(msg);
    }
}
