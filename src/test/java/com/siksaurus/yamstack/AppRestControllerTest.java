package com.siksaurus.yamstack;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.ResultActions;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


//@ExtendWith(SpringExtension.class)
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AppRestControllerTest extends ControllerTest{

//    @Autowired
//    private TestRestTemplate restTemplate;

    @Test
    public void Profile확인() throws Exception {
//        //when
//        String profile = this.restTemplate.getForObject("/profile",String.class);
//
//        //then
//        assertThat(profile).isEqualTo("local");

        //when
        ResultActions result = mockMvc.perform(get("/profile"));

        //then
        result
                .andDo(print())
                .andExpect(status().isOk());
    }
}
