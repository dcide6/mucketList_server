package com.siksaurus.yamstack.account.controller;

import com.siksaurus.yamstack.account.domain.Account;
import com.siksaurus.yamstack.account.service.AccountService;
import com.siksaurus.yamstack.account.service.LoginService;
import com.siksaurus.yamstack.global.CommonResponse;
import com.siksaurus.yamstack.global.exception.LoginFailedException;
import com.siksaurus.yamstack.global.security.JwtAuthToken;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Optional;

@RestController
@RequestMapping("/login")
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;
    private final AccountService accountService;

    @PostMapping("/sign")
    public ResponseEntity<CommonResponse> login(@RequestBody AccountDTO.loginDTO loginDTO) {

        Optional<Account> account = loginService.login(loginDTO.getEmail(), loginDTO.getPassword());

        if (account.isPresent()) {

            CommonResponse response;
            if (!account.get().isEmailChecked()) {
                response = CommonResponse.builder()
                        .code("LOGIN_SUCCESS")
                        .status(200)
                        .message("Identity verification is required")
                        .build();
            } else {
                Account user = account.get();
                user.setLastLoginDate(LocalDate.now());
                accountService.saveAccount(user);
                JwtAuthToken jwtAuthToken = (JwtAuthToken) loginService.createAuthToken(account.get());
                response = CommonResponse.builder()
                        .code("LOGIN_SUCCESS")
                        .status(200)
                        .message(jwtAuthToken.getToken())
                        .build();
            }
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response);

        } else {
            throw new LoginFailedException();
        }
    }

    @GetMapping("/emailCheck/{email}")
    public ResponseEntity<CommonResponse> checkDuplicateEmail(@PathVariable String email) {

        CommonResponse response = CommonResponse.builder()
                .code("CHECK_ID")
                .status(200)
                .message(String.valueOf(accountService.checkDuplicateEmail(email)))
                .build();

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(response);
    }

    @GetMapping("/nameCheck/{name}")
    public ResponseEntity<CommonResponse> checkDuplicateName(@PathVariable String name) {

        CommonResponse response = CommonResponse.builder()
                .code("CHECK_NAME")
                .status(200)
                .message(String.valueOf(accountService.checkDuplicateName(name)))
                .build();

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(response);
    }

    @PostMapping("/join")
    public ResponseEntity<CommonResponse> createAccount(@RequestBody AccountDTO.CreateAccountDTO dto) {

        CommonResponse response;
        if (accountService.checkDuplicateEmail(dto.getEmail()) && accountService.checkDuplicateName(dto.getName())) {
            String authCode = loginService.authMailSend(dto.getEmail(), dto.getName());
            Account account = dto.toEntity();
            account.setEmailChecked(false);
            account.setAuthCode(authCode);
            accountService.addAccount(account);

            response = CommonResponse.builder()
                    .code("JOIN_SUCCESS")
                    .status(200)
                    .message("JOIN_SUCCESS")
                    .build();

        } else {
            response = CommonResponse.builder()
                    .code("DUPLICATED_ACCOUNT")
                    .status(400)
                    .message("DUPLICATED_ACCOUNT")
                    .build();
        }
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(response);
    }

    @PostMapping("/identify")
    public ResponseEntity<CommonResponse> accountIdentify(@RequestBody AccountDTO.IdentifyAccountDTO dto) {

        CommonResponse response;
        if (loginService.checkAuthCode(dto.getEmail(), dto.getAuthCode())) {
            response = CommonResponse.builder()
                    .code("IDENTIFY_SUCCESS")
                    .status(200)
                    .message("IDENTIFY_SUCCESS")
                    .build();
        } else {
            response = CommonResponse.builder()
                    .code("IDENTIFY_FAIL")
                    .status(400)
                    .message("IDENTIFY_FAIL")
                    .build();
        }

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(response);
    }

    @PostMapping("/authCode")
    public ResponseEntity<CommonResponse> authCodeResend(@RequestBody AccountDTO.UpdateAccountDTO dto) {
        Account account = accountService.getAccountByEmail(dto.getEmail());
        String authCode = loginService.authMailSend(dto.getEmail(), dto.getName());
        account.setAuthCode(authCode);
        account.setEmailChecked(false);
        accountService.saveAccount(account);

        CommonResponse response = CommonResponse.builder()
                .code("AUTHCODE_RESEND")
                .status(200)
                .message("AUTHCODE_RESEND")
                .build();

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(response);
    }
}
