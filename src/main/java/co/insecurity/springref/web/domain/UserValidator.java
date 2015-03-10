package co.insecurity.springref.web.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import co.insecurity.springref.security.service.PassCheckService;

@Component
public class UserValidator implements Validator {

	private static final Logger LOG = 
			LoggerFactory.getLogger(UserValidator.class);

	
	@Autowired
	private PassCheckService passCheckService;
	
	
	@Override
	public boolean supports(Class<?> clazz) {
		return User.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		LOG.debug("Validating object: {}", target.toString());
		if (target != null && supports(target.getClass())) {
			User user = (User) target;
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "username", "field.empty");
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "firstName", "field.empty");
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "lastName", "field.empty");
			if (passwordFieldChanged(user)) {
				validatePasswordFields(user, errors);
			}
		}
	}

	private void validatePasswordFields(User user, Errors errors) {
		LOG.debug("Validating password fields for user: {}", user.toString());
		String password = user.getPassword();
		String passwordConfirm = user.getPasswordConfirm();
		if ((password == null || password.isEmpty()) 
				|| (passwordConfirm == null || passwordConfirm.isEmpty())) {
			LOG.debug("Password validation failed: unmatched passwords.");
			errors.rejectValue("password", "field.empty", 
					"Password may not be empty!!");
			errors.rejectValue("passwordConfirm", "field.empty", 
					"Password may not be empty!");
		}
		else if (!(password.equals(passwordConfirm))) {
			LOG.debug("Password validation failed: unmatched passwords.");
			errors.rejectValue("password", "field.unmatchedPassword", 
					"Password fields must match!");
			errors.rejectValue("passwordConfirm", "field.unmatchedPassword", 
					"Password fields must match!");
		}
		else if (!isPasswordComplex(user)) {
			LOG.debug("Password validation failed: password does not meet complexity requirements.");
			errors.rejectValue("password", "field.policy", 
					"Password must be at least 9 characters long, and must contain a mixture "
					+ "of upper- and lower-case letters, and numbers.");
			errors.rejectValue("passwordConfirm", "field.policy", 
					"Password must be at least 9 characters long, and must contain a mixture "
					+ "of upper- and lower-case letters, and numbers.");
		}
		else if (passCheckService.isCommon(password)) {
			LOG.debug("Password validation failed: password is too common.");
			errors.rejectValue("password", "field.policy", 
					"Password is too common and could be easily guessed.");
			errors.rejectValue("passwordConfirm", "field.policy", 
					"Password is too common and could be easily guessed.");
		}
	}
	
	/**
	 * Checks if password meets length and complexity requirements.
	 * 
	 * The function first checks if the password is at least:
	 * 		1.) 9 characters in length
	 * 		2.) Contains at least 1 lower case letter
	 * 		3.) Contains at least 1 upper case letter
	 * 		4.) Contains at least 1 number
	 * 
	 * The function then performs some (very basic) complexity checks, 
	 * to see if the password contains, or is contained in, the user's 
	 * username, first name, or last name fields.
	 * 
	 * 
	 * @param user the {@code User} object to check the password field for
	 * @return true if the password meets all complexity requirements, 
	 * otherwise returns false
	 */
	private boolean isPasswordComplex(User user) {
		String password = user.getPassword();
		String username = user.getUsername();
		String firstName = user.getFirstName();
		String lastName = user.getLastName();
		
		if (password.length() < 9)
			return false;
		if (!password.matches("^.*[A-Z].*$"))
			return false;
		if (!password.matches("^.*[a-z].*$"))
			return false;
		if (!password.matches("^.*[0-9].*$"))
			return false;
		String lowerPassword = password.toLowerCase();
		if (lowerPassword.contains(username.toLowerCase()))
			return false;
		if (lowerPassword.contains(firstName.toLowerCase()))
			return false;
		if (lowerPassword.contains(lastName.toLowerCase()))
			return false;
		String userString = username.concat(firstName)
				.concat(lastName);
		if (userString.toLowerCase().contains(lowerPassword))
			return false;
		
		return true;
	}
	
	private boolean passwordFieldChanged(User user) {
		LOG.debug("Checking if password fields changed for user: {}", user.toString());
		return (user.getPassword() != null && !(user.getPassword().isEmpty()) ||
				user.getPasswordConfirm() != null && !(user.getPasswordConfirm().isEmpty()));
	}
}
