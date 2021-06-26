package com.siksaurus.yamstack.account.controller;


import com.siksaurus.yamstack.ControllerTest;
import com.siksaurus.yamstack.account.domain.Account;
import com.siksaurus.yamstack.account.domain.AccountRole;
import com.siksaurus.yamstack.account.service.AccountService;
import com.siksaurus.yamstack.review.s3upload.S3Uploader;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class AccountControllerTest extends ControllerTest {

    @Test
    public void getAccount() throws Exception {

        //given
        AccountRole role = AccountRole.USER;

        Date expiredDate = Date.from(LocalDateTime.now().plusMinutes(30).atZone(ZoneId.systemDefault()).toInstant());
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("x-auth-token", makeJwtAuthToken(role, expiredDate));

        Account account = Account.builder()
                .email("test@aaa.bbb")
                .password("1234")
                .name("test")
                .role(AccountRole.USER)
                .build();

        given(accountService.getAccountByEmail("test@aaa.bbb")).willReturn(account);

        //when
        ResultActions result = mockMvc.perform(get("/api/v1/account/test@aaa.bbb").headers(httpHeaders));

        //then
        result
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void deleteAccount() throws Exception {

        //given
        AccountRole role = AccountRole.USER;

        Date expiredDate = Date.from(LocalDateTime.now().plusMinutes(30).atZone(ZoneId.systemDefault()).toInstant());
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("x-auth-token", makeJwtAuthToken(role, expiredDate));

        //when
        ResultActions result = mockMvc.perform(delete("/api/v1/account/test@aaa.bbb").headers(httpHeaders));

        //then
        result
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
}
