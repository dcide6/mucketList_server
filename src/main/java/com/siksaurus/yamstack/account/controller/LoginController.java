package com.siksaurus.yamstack.account.controller;

import com.siksaurus.yamstack.account.domain.Account;
import com.siksaurus.yamstack.account.service.LoginService;
import com.siksaurus.yamstack.global.CommonResponse;
import com.siksaurus.yamstack.global.exception.LoginFailedException;
import com.siksaurus.yamstack.global.security.JwtAuthToken;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/login")
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;

    @PostMapping
    public ResponseEntity<CommonResponse> login(@RequestBody AccountDTO.loginDTO loginDTO) {

        Optional<Account> account = loginService.login(loginDTO.getId(), loginDTO.getPassword());

        if (account.isPresent()) {

            JwtAuthToken jwtAuthToken = (JwtAuthToken) loginService.createAuthToken(account.get());
            CommonResponse response = CommonResponse.builder()
                    .code("LOGIN_SUCCESS")
                    .status(200)
                    .message(jwtAuthToken.getToken())
                    .build();

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response);

        } else {
            throw new LoginFailedException();
        }
    }
}
