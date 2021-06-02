package com.siksaurus.yamstack.user.controller;

import com.siksaurus.yamstack.user.domain.User;
import com.siksaurus.yamstack.user.domain.UserRole;
import com.siksaurus.yamstack.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Set;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
public class UserControllerTest {
    @Autowired
    protected MockMvc mockMvc;

    @MockBean
    protected UserService userService;

    @Test
    public void getUser() throws Exception {

        //given
        User user = User.builder()
                .id("test@aaa.bbb")
                .password("1234")
                .name("test")
                .roles(Set.of(UserRole.USER))
                .build();

        given(userService.getUser("test@aaa.bbb")).willReturn(user);

        //when
        ResultActions result = mockMvc.perform(get("/user/test@aaa.bbb"));

        //then
        result
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
}
