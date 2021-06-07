package com.siksaurus.yamstack.account.controller;

import com.siksaurus.yamstack.account.domain.Account;
import com.siksaurus.yamstack.account.domain.AccountRole;
import com.siksaurus.yamstack.account.service.AccountService;
import com.siksaurus.yamstack.global.security.JwtAuthTokenProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class AccountControllerTest {
    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected JwtAuthTokenProvider jwtAuthTokenProvider;

    @MockBean
    protected AccountService accountService;

    private String makeJwtAuthToken(AccountRole role, Date expiredDate) {
        return jwtAuthTokenProvider.createAuthToken("test",role, expiredDate).getToken();
    }

    @Test
    public void getAccount() throws Exception {

        //given
        AccountRole role = AccountRole.USER;

        Date expiredDate = Date.from(LocalDateTime.now().plusMinutes(30).atZone(ZoneId.systemDefault()).toInstant());
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("x-auth-token", makeJwtAuthToken(role, expiredDate));

        Account account = Account.builder()
                .id("test@aaa.bbb")
                .password("1234")
                .name("test")
                .role(AccountRole.USER)
                .build();

        given(accountService.getAccount("test@aaa.bbb")).willReturn(account);

        //when
        ResultActions result = mockMvc.perform(get("/api/v1/account/test@aaa.bbb").headers(httpHeaders));

        //then
        result
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
}
