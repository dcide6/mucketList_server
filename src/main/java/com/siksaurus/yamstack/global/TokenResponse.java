package com.siksaurus.yamstack.global;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Builder
public class TokenResponse {
    private String message;
    private String code;
    private int status;
    private String accessToken;
    private String refreshToken;
}
