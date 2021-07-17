package com.siksaurus.yamstack.account.controller;

import com.siksaurus.yamstack.account.domain.Account;
import com.siksaurus.yamstack.account.domain.AccountStat;
import com.siksaurus.yamstack.account.domain.repository.AccountStatRepository;
import com.siksaurus.yamstack.account.service.AccountService;
import com.siksaurus.yamstack.global.CommonResponse;
import com.siksaurus.yamstack.global.security.JwtAuthToken;
import com.siksaurus.yamstack.global.security.JwtAuthTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/account")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;
    private final AccountStatRepository accountStatRepository;
    private final JwtAuthTokenProvider jwtAuthTokenProvider;

    @GetMapping
    public ResponseEntity<Account> getAccountByEmail(@RequestHeader(value = "x-auth-token") String token) {

        JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token);
        String email = (String) jwtAuthToken.getData().get("sub");

        Account account = accountService.getAccountByEmail(email);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(account);
    }

    @PutMapping
    public ResponseEntity<Account> updateAccountByEmail(@RequestHeader(value = "x-auth-token") String token,
                                                        @RequestBody AccountDTO.UpdateAccountDTO dto) {

        JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token);
        String email = (String) jwtAuthToken.getData().get("sub");

        Account account_rst = null;
        if(dto.getNewPassword() != null && !dto.getNewPassword().equals("")) {
            account_rst = accountService.changePassword(email, dto.getNewPassword());
        }
        if(dto.getNewName() != null && !dto.getNewName().equals("")) {
            Account account = accountService.getAccountByEmail(email);
            account.setName(dto.getNewName());
            account_rst = accountService.saveAccount(account);
        }

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(account_rst);

    }

    @DeleteMapping
    public ResponseEntity<CommonResponse> deleteAccountByEmail(@RequestHeader(value = "x-auth-token") String token) {

        JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token);
        String email = (String) jwtAuthToken.getData().get("sub");

        long id = accountService.deleteAccountByEmail(email);
        accountStatRepository.save(AccountStat.builder().accountId(id).date(LocalDate.now()).isJoin(false).build());

        CommonResponse response = CommonResponse.builder()
                .code("DELETE ACCOUNT SUCCESS")
                .status(200)
                .message("DELETE ACCOUNT SUCCESS")
                .build();

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(response);
    }

}
