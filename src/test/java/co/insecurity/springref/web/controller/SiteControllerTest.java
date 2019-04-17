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
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class SiteControllerTest {

    @Autowired
    private MockMvc mvc;

    @Test
    @WithAnonymousUser
    public void thatUnauthenticatedUserCanAccessRoot() throws Exception {
        this.mvc.perform(get("https://localhost/")).andExpect(status().isOk());
    }

    @Test
    @WithAnonymousUser
    public void thatUnauthenticatedUserCanAccessLogin() throws Exception {
        this.mvc.perform(get("https://localhost/login")).andExpect(status().isOk());
    }

    @Test
    public void thatBuiltinAdminCanLogin() throws Exception {
        mvc.perform(
                formLogin("https://localhost/login")
                        .user("username", "admin")
                        .password("password", "admin"))
                .andExpect(
                        authenticated()
                                .withUsername("admin")
                                .withRoles("USER", "ADMIN"));
    }
}