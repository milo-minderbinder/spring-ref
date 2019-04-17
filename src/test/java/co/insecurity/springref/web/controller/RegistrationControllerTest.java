package co.insecurity.springref.web.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class RegistrationControllerTest {

    @Autowired
    private MockMvc mvc;

    @Test
    @WithAnonymousUser
    public void thatUnauthenticatedUserCanAccessRegistration() throws Exception {
        this.mvc.perform(get("https://localhost/register")).andExpect(status().isOk());
    }

    @Test
    @WithAnonymousUser
    public void thatUnauthenticatedUserCanRegister() throws Exception {
        String username = "bob";
        String password = "ABCdef123456790";
        this.mvc.perform(
                post("https://localhost/register")
                        .param("username", username)
                        .param("password", password)
                        .param("passwordConfirm", password)
                        .param("firstName", "Bob")
                        .param("lastName", "Roberts")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andDo(result -> mvc.perform(
                        formLogin("https://localhost/login")
                                .user("username", username)
                                .password("password", password))
                        .andExpect(
                                authenticated()
                                        .withUsername(username)
                                        .withRoles("USER")));

    }
}