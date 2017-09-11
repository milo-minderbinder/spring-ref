package co.insecurity.springref.web.domain;

import co.insecurity.policy.PasswordPolicy;
import co.insecurity.springref.core.domain.UserRole;
import co.insecurity.springref.security.policy.SimplePasswordPolicy;
import org.junit.*;

public class UserValidatorTest {

    private static PasswordPolicy passwordPolicy;

    private User user;
    private UserValidator validator;

    @BeforeClass
    public static void setUpClass() {
        passwordPolicy = new SimplePasswordPolicy(false);
    }

    @AfterClass
    public static void tearDownClass() {
        passwordPolicy = null;
    }

    @Before
    public void setUp() {
        user = new User();
        user.setUsername("test");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setPassword("Password1");
        user.getRoles().add(UserRole.USER);

        validator = new UserValidator(passwordPolicy);
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
