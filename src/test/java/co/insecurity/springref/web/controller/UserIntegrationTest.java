package co.insecurity.springref.web.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.view.InternalResourceViewResolver;


public class UserIntegrationTest {

	MockMvc mockMvc;
	
	@InjectMocks
	UserController controller;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		mockMvc = standaloneSetup(controller)
				.setViewResolvers(viewResolver())
				.build();
	}
	
	private InternalResourceViewResolver viewResolver() {
		InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
		viewResolver.setPrefix("/WEB-INF/views");
		viewResolver.setSuffix(".html");
		return viewResolver;
	}
	
	/**
	 * Obviously a pointless test.
	 * 
	 * @throws Exception if everything is terrible, and the world is unfair
	 */
	@Test
	public void thatViewProfileGetsUser() throws Exception {
		//mockMvc.perform(get("/user/viewProfile"));
	}
}
