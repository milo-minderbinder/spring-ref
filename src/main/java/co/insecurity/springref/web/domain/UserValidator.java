package co.insecurity.springref.web.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

public class UserValidator implements Validator {

	private static final Logger LOG = 
			LoggerFactory.getLogger(UserValidator.class);

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
				validateNewPasswordFields(user, errors);
			}
		}
	}

	private void validateNewPasswordFields(User user, Errors errors) {
		LOG.debug("Validating password fields for user: {}", user.toString());
		String newPassword = user.getPassword();
		String newPasswordConfirm = user.getPasswordConfirm();
		if (newPassword == null || newPasswordConfirm == null) {
			LOG.debug("Edit profile unsuccessful: unmatched passwords.");
			errors.rejectValue("password", "field.empty", "Password may not be empty!!");
			errors.rejectValue("passwordConfirm", "field.empty", "Password may not be empty!");
		}
		else if (!(newPassword.equals(newPasswordConfirm))) {
			LOG.debug("Edit profile unsuccessful: unmatched passwords.");
			errors.rejectValue("password", "field.unmatchedPassword", "Password fields must match!");
			errors.rejectValue("passwordConfirm", "field.unmatchedPassword", "Password fields must match!");
		}
		else if (newPassword.isEmpty()) {
			LOG.debug("Edit profile unsuccessful: blank password.");
			errors.rejectValue("password", "field.empty", "Password may not be blank!");
			errors.rejectValue("passwordConfirm", "field.empty", "Password may not be blank!");
		}
	}
	
	private boolean passwordFieldChanged(User user) {
		LOG.debug("Checking if password fields changed for user: {}", user.toString());
		return (user.getPassword() != null && !(user.getPassword().isEmpty()) ||
				user.getPasswordConfirm() != null && !(user.getPasswordConfirm().isEmpty()));
	}
}
