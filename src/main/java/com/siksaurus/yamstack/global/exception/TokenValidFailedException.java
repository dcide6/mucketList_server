package com.siksaurus.yamstack.global.exception;

public class TokenValidFailedException extends RuntimeException {

    public TokenValidFailedException(){
        super(ErrorCode.TOKEN_GENERATION_FAILED.getMessage());
    }

    private TokenValidFailedException(String msg){
        super(msg);
    }
}
