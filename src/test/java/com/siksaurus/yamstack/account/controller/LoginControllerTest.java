package com.siksaurus.yamstack.account.controller;

import com.siksaurus.yamstack.ControllerTest;
import com.siksaurus.yamstack.account.domain.Account;
import com.siksaurus.yamstack.account.domain.AccountRole;
import com.siksaurus.yamstack.account.service.AccountService;
import com.siksaurus.yamstack.account.service.LoginService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;

public class LoginControllerTest extends ControllerTest {

    @MockBean
    LoginService loginService;

    @MockBean
    AccountService accountService;

    @Test
    public void Join() throws Exception {

        Account account = Account.builder()
                .id("test@aaa.bbb")
                .password("1234")
                .name("test")
                .role(AccountRole.USER)
                .build();
        account.setEmailChecked(false);

    }

}
