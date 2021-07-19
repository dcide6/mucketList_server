package com.siksaurus.yamstack.account.controller;

import com.siksaurus.yamstack.account.domain.Account;
import com.siksaurus.yamstack.account.domain.AccountRole;
import com.siksaurus.yamstack.account.domain.AccountStat;
import com.siksaurus.yamstack.account.domain.repository.AccountStatRepository;
import com.siksaurus.yamstack.account.service.AccountService;
import com.siksaurus.yamstack.account.service.LoginService;
import com.siksaurus.yamstack.global.CommonResponse;
import com.siksaurus.yamstack.global.TokenResponse;
import com.siksaurus.yamstack.global.exception.LoginFailedException;
import com.siksaurus.yamstack.global.security.JwtAuthToken;
import com.siksaurus.yamstack.global.security.JwtAuthTokenProvider;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/login")
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;
    private final AccountService accountService;
    private final AccountStatRepository accountStatRepository;

    @PostMapping("/sign")
    public ResponseEntity<TokenResponse> login(@RequestBody AccountDTO.loginDTO loginDTO) {

        Optional<Account> account = loginService.login(loginDTO.getEmail(), loginDTO.getPassword());

        TokenResponse response;
        if (!account.get().isEmailChecked()) {
            response = TokenResponse.builder()
                    .code("LOGIN_SUCCESS")
                    .status(200)
                    .message("Identity verification is required")
                    .build();
        } else {
            Account user = account.get();
            user.setLastLoginDate(LocalDate.now());
            accountService.saveAccount(user);
            JwtAuthToken jwtAuthToken = loginService.createAuthToken(account.get());
            JwtAuthToken refreshToken = loginService.createRefreshToken(account.get());
            response = TokenResponse.builder()
                    .code("LOGIN_SUCCESS")
                    .status(200)
                    .message("LOGIN_SUCCESS")
                    .accessToken(jwtAuthToken.getToken())
                    .refreshToken(refreshToken.getToken())
                    .build();
        }

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(response);

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
            Account account_rst = accountService.addAccount(account);
            accountStatRepository.save(AccountStat.builder().accountId(account_rst.getId()).date(LocalDate.now()).isJoin(true).build());

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
    public ResponseEntity<TokenResponse> accountIdentify(@RequestBody AccountDTO.IdentifyAccountDTO dto) {

        TokenResponse response;
        if (loginService.checkAuthCode(dto.getEmail(), dto.getAuthCode())) {
            Account account = new Account();
            account.setEmail(dto.getEmail());
            account.setRole(AccountRole.USER);
            JwtAuthToken jwtAuthToken = loginService.createAuthToken(account);
            JwtAuthToken refreshToken = loginService.createRefreshToken(account);
            response = TokenResponse.builder()
                    .code("IDENTIFY_SUCCESS")
                    .status(200)
                    .message("IDENTIFY_SUCCESS")
                    .accessToken(jwtAuthToken.getToken())
                    .refreshToken(refreshToken.getToken())
                    .build();
        } else {
            response = TokenResponse.builder()
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
    public ResponseEntity<CommonResponse> authCodeResend(@RequestBody AccountDTO.resendAuthCodeDTO dto) {
        Account account = accountService.getAccountByEmail(dto.getEmail());
        String authCode = loginService.authMailSend(dto.getEmail(), account.getName());
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

    @PostMapping("/refreshToken")
    public ResponseEntity<TokenResponse> refreshToken(@RequestBody AccountDTO.refreshTokenDTO dto) {
        Map<String, String> tokens = loginService.refreshAccessToken(dto.getRefreshToken());

        TokenResponse response = TokenResponse.builder()
                .code("REFRESH ACCESS TOKEN")
                .status(200)
                .message("REFRESH ACCESS TOKEN")
                .accessToken(tokens.get("access"))
                .refreshToken(tokens.get("refresh"))
                .build();

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(response);
    }
}
