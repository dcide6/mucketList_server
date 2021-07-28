package com.siksaurus.yamstack.account.controller;

import com.siksaurus.yamstack.ControllerTest;
import com.siksaurus.yamstack.account.domain.Account;
import com.siksaurus.yamstack.account.domain.AccountRole;
import com.siksaurus.yamstack.account.service.AccountService;
import com.siksaurus.yamstack.account.service.LoginService;
import com.siksaurus.yamstack.account.service.MailService;
import com.siksaurus.yamstack.global.security.JwtAuthToken;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


public class LoginControllerTest extends ControllerTest {

    @MockBean
    LoginService loginService;

    @MockBean
    MailService mailService;

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
        JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.createAuthToken("test@aaa.bbb",AccountRole.USER, "access", expiredDate);
        JwtAuthToken refreshToken = jwtAuthTokenProvider.createAuthToken("test@aaa.bbb",AccountRole.USER, "refresh", expiredDate);


        given(loginService.login(loginDTO.getEmail(), loginDTO.getPassword())).willReturn(Optional.ofNullable(account));
        given(loginService.createAuthToken(account)).willReturn(jwtAuthToken);
        given(loginService.createRefreshToken(account)).willReturn(refreshToken);

        //when
        ResultActions result = mockMvc.perform(post("/login/sign")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDTO)));

        //then
        result
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.accessToken").value(jwtAuthToken.getToken()));
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
        account.setId(123);
        account.setEmailChecked(false);

        given(accountService.checkDuplicateEmail(dto.getEmail())).willReturn(true);
        given(accountService.checkDuplicateName(dto.getName())).willReturn(true);
        given(mailService.authMailSend(dto.getEmail(), dto.getName())).willReturn("123456");
        given(accountService.addAccount(any())).willReturn(account);

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

        Account account = Account.builder()
                .email("test@aaa.bbb")
                .password("1234")
                .name("test")
                .role(AccountRole.USER)
                .build();
        account.setId(123);
        account.setEmailChecked(false);

        Date expiredDate = Date.from(LocalDateTime.now().plusMinutes(30).atZone(ZoneId.systemDefault()).toInstant());
        JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.createAuthToken("test@aaa.bbb",role, "access", expiredDate);
        JwtAuthToken refreshToken = jwtAuthTokenProvider.createAuthToken("test@aaa.bbb",role, "refresh", expiredDate);

        AccountDTO.IdentifyAccountDTO dto = new AccountDTO.IdentifyAccountDTO();
        dto.setEmail("test@aaa.bbb");
        dto.setAuthCode("1234565");

        given(accountService.getAccountByEmail(dto.getEmail())).willReturn(account);
        given(loginService.checkAuthCode(dto.getEmail(), dto.getAuthCode())).willReturn(true);
        given(loginService.createAuthToken(any())).willReturn(jwtAuthToken);
        given(loginService.createRefreshToken(any())).willReturn(refreshToken);

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
        given(mailService.authMailSend(dto.getEmail(), account.getName())).willReturn("123456");


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

    @Test
    public void refreshToken() throws Exception {
        //given
        AccountRole role = AccountRole.USER;

        Date expiredDate = Date.from(LocalDateTime.now().plusMinutes(30).atZone(ZoneId.systemDefault()).toInstant());
        JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.createAuthToken("test@aaa.bbb",role, "access", expiredDate);
        JwtAuthToken refreshToken = jwtAuthTokenProvider.createAuthToken("test@aaa.bbb",role, "refresh", expiredDate);

        Map<String, String> tokens = new HashMap<>();
        tokens.put("access", jwtAuthToken.getToken());
        tokens.put("refresh", refreshToken.getToken());

        AccountDTO.refreshTokenDTO dto = new AccountDTO.refreshTokenDTO();
        dto.setRefreshToken(refreshToken.getToken());

        given(loginService.refreshAccessToken(any())).willReturn(tokens);

        //when
        ResultActions result = mockMvc.perform(post("/login/refreshToken")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)));

        //then
        result
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

}
