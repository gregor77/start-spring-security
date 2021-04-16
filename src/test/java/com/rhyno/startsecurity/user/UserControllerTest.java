package com.rhyno.startsecurity.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rhyno.startsecurity.StartSecurityApplication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@SpringBootTest(classes = StartSecurityApplication.class)
class UserControllerTest {
    private static final String ANY_USER_NAME = "rhyno";
    private static final String ANY_EMAIL = "rhyno@mail.com";
    private static final String ANY_PHONE_NUMBER = "010-1234-5678";
    private static final long ANY_ID = 1;

    @Autowired
    private MockMvc mockmvc;

    @MockBean
    private UserService mockUserService;

    @Autowired
    private ObjectMapper objectMapper;

    private Optional<User> user;

    @BeforeEach
    void setUp() {
        user = Optional.of(User.builder()
                .name(ANY_USER_NAME)
                .email(ANY_EMAIL)
                .phoneNumber(ANY_PHONE_NUMBER)
                .build());
    }

    @Nested
    class getUser {
        /**
         * 데이터를 변경하는 http method(POST, PUT, DELETE)가 아니기 때문에
         * csrf token이 없더라도 문제없다.
         */
        @Test
        @DisplayName("should return found user")
        @WithMockUser(username = "user", password = "password", roles = "USER")
        void normalCase() throws Exception {
            given(mockUserService.getUser(ANY_ID)).willReturn(user.get());

            MvcResult mvcResult = mockmvc.perform(get("/v1/user/" + ANY_ID))
                    .andExpect(status().isOk())
                    .andReturn();

            User foundUser = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), User.class);
            assertThat(foundUser).isEqualTo(user.get());
        }
    }

    @Nested
    class createUser {
        /**
         * 리소스를 변경하는 http method(POST, PUT, DELETE)이므로
         * csrf token이 없으면 403 Forbidden 리턴한다.
         */
        @Test
        @DisplayName("should return error without csrf token")
        void csrfError() throws Exception {
            mockmvc.perform(post("/v1/user")
                    .content("{\n" +
                            "  \"name\": \"rhyno\",\n" +
                            "  \"email\": \"rhyno@mail.com\",\n" +
                            "  \"phoneNumber\": \"010-1234-5678\"\n" +
                            "}")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("should return created user with csrf token")
        @WithMockUser(username = "user", password = "password", roles = "USER")
        void normalCase() throws Exception {
            given(mockUserService.getUser(ANY_EMAIL)).willReturn(user);
            given(mockUserService.createUser(user.get())).willReturn(user.get());

            MvcResult mvcResult = mockmvc.perform(post("/v1/user")
                    .content("{\n" +
                            "  \"name\": \"rhyno\",\n" +
                            "  \"email\": \"rhyno@mail.com\",\n" +
                            "  \"phoneNumber\": \"010-1234-5678\"\n" +
                            "}")
                    .contentType(MediaType.APPLICATION_JSON)
                    .with(csrf()))
                    .andExpect(status().isCreated())
                    .andReturn();

            User createUser = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), User.class);
            assertThat(createUser).isEqualTo(user.get());
        }
    }
}