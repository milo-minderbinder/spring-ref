package co.insecurity.springref.web.domain;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import co.insecurity.springref.core.domain.UserRole;

public class UserValidatorTest {

	private User user;
	private UserValidator validator;
	
	@Before
	public void setUp() {
		user = new User();
		user.setUsername("test");
		user.setFirstName("John");
		user.setLastName("Doe");
		user.setPassword("Password1");
		user.getRoles().add(UserRole.USER);
		
		validator = new UserValidator();
	}
	
	@After
	public void tearDown() {
		user = null;
		validator = null;
	}
	
	@Test
	public void thatUserSupported() {
		Assert.assertTrue("Failure - userValidator should support User", 
				validator.supports(user.getClass()));
	}
}
