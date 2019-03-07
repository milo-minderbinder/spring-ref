package co.insecurity.springref.web.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class UserIntegrationTest {

    @Autowired
    private MockMvc mvc;

    /**
     * Obviously a pointless test.
     *
     * @throws Exception if everything is terrible, and the world is unfair
     */
    @Test
    @WithMockUser(roles = "USER")
    public void thatViewProfileGetsUser() throws Exception {
        this.mvc.perform(get("https://localhost/user/viewProfile")).andExpect(status().isOk());
    }
}
