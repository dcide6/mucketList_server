package com.siksaurus.yamstack.account.controller;

import com.siksaurus.yamstack.account.domain.Account;
import com.siksaurus.yamstack.account.service.AccountService;
import com.siksaurus.yamstack.global.CommonResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/account")
public class AccountController {

    @Autowired
    AccountService accountService;

    @GetMapping("/{email}")
    public ResponseEntity<Account> getAccountByEmail(@PathVariable String email) {

        Account account = accountService.getAccountByEmail(email);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(account);
    }

    @DeleteMapping("/{email}")
    public ResponseEntity<CommonResponse> deleteAccountByEmail(@PathVariable String email) {

        accountService.deleteAccountByEmail(email);
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
