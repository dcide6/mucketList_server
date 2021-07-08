package com.siksaurus.yamstack.account.controller;

import com.siksaurus.yamstack.ControllerTest;
import com.siksaurus.yamstack.account.domain.Account;
import com.siksaurus.yamstack.account.domain.AccountRole;
import com.siksaurus.yamstack.account.service.AccountService;
import com.siksaurus.yamstack.account.service.LoginService;
import com.siksaurus.yamstack.global.security.JwtAuthToken;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


public class LoginControllerTest extends ControllerTest {

    @MockBean
    LoginService loginService;

    @Test
    public void login() throws Exception {

        //given
        AccountDTO.loginDTO loginDTO = new AccountDTO.loginDTO();
        loginDTO.setEmail("test@aaa.bbb");
        loginDTO.setPassword("1234");

        Account account = Account.builder()
                .email("test@aaa.bbb")
                .name("test")
                .password("1234")
                .role(AccountRole.USER)
                .build();
        account.setEmailChecked(true);

        Date expiredDate = Date.from(LocalDateTime.now().plusMinutes(30).atZone(ZoneId.systemDefault()).toInstant());
        JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.createAuthToken("test@aaa.bbb",AccountRole.USER, expiredDate);

        given(loginService.login(loginDTO.getEmail(), loginDTO.getPassword())).willReturn(Optional.ofNullable(account));
        given(loginService.createAuthToken(account)).willReturn(jwtAuthToken);

        //when
        ResultActions result = mockMvc.perform(post("/login/sign")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDTO)));

        //then
        result
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value(jwtAuthToken.getToken()));
    }

    @Test
    public void checkDuplicateId() throws Exception {

        //given
        given(accountService.checkDuplicateEmail("test@aaa.bbb")).willReturn(true);

        //when
        ResultActions result = mockMvc.perform(get("/login/emailCheck/test@aaa.bbb"));

        //then
        result
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("true"));
    }

    @Test
    public void checkDuplicateName() throws Exception {

        //given
        given(accountService.checkDuplicateName("test")).willReturn(true);

        //when
        ResultActions result = mockMvc.perform(get("/login/nameCheck/test"));

        //then
        result
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("true"));
    }

    @Test
    public void createAccount() throws Exception {

        //given
        AccountDTO.CreateAccountDTO dto = new AccountDTO.CreateAccountDTO();
        dto.setEmail("test@aaa.bbb");
        dto.setPassword("1234");
        dto.setName("test");

        Account account = Account.builder()
                .email("test@aaa.bbb")
                .password("1234")
                .name("test")
                .role(AccountRole.USER)
                .build();
        account.setEmailChecked(false);

        given(accountService.checkDuplicateEmail(dto.getEmail())).willReturn(true);
        given(accountService.checkDuplicateName(dto.getName())).willReturn(true);
        given(loginService.authMailSend(dto.getEmail(), dto.getName())).willReturn("123456");
        given(accountService.addAccount(dto.toEntity())).willReturn(account);

        //when
        ResultActions result = mockMvc.perform(post("/login/join")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)));

        //then
        result
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value("JOIN_SUCCESS"));


    }

    @Test
    public void accountIdentify() throws Exception {

        //given
        AccountRole role = AccountRole.USER;

        Date expiredDate = Date.from(LocalDateTime.now().plusMinutes(30).atZone(ZoneId.systemDefault()).toInstant());
        JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.createAuthToken("test@aaa.bbb",role, expiredDate);

        AccountDTO.IdentifyAccountDTO dto = new AccountDTO.IdentifyAccountDTO();
        dto.setEmail("test@aaa.bbb");
        dto.setAuthCode("1234565");

        given(loginService.checkAuthCode(dto.getEmail(), dto.getAuthCode())).willReturn(true);
        given(loginService.createAuthToken(any())).willReturn(jwtAuthToken);

        //when
        ResultActions result = mockMvc.perform(post("/login/identify")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)));

        //then
        result
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value("IDENTIFY_SUCCESS"));
    }

    @Test
    public void authCodeResend() throws Exception {

        //given
        AccountDTO.resendAuthCodeDTO dto = new AccountDTO.resendAuthCodeDTO();
        dto.setEmail("test@aaa.bbb");

        Account account = Account.builder()
                .email("test@aaa.bbb")
                .name("test")
                .password("1234")
                .role(AccountRole.USER)
                .build();

        given(accountService.getAccountByEmail(dto.getEmail())).willReturn(account);
        given(loginService.authMailSend(dto.getEmail(), account.getName())).willReturn("123456");


        //when
        ResultActions result = mockMvc.perform(post("/login/authCode")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)));

        //then
        result
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value("AUTHCODE_RESEND"));
    }

}
