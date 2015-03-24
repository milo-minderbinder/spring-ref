package co.insecurity.springref.web.domain;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import co.insecurity.security.policy.PasswordPolicy;
import co.insecurity.security.policy.assertion.PolicyAssertion;


@Component
public class UserValidator implements Validator {

	private static final Logger LOG = 
			LoggerFactory.getLogger(UserValidator.class);

	@Autowired
	private PasswordPolicy passwordPolicy;
	
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
		Set<PolicyAssertion.Result> violations = 
				PasswordPolicy.getViolations(passwordPolicy.evaluate(password));
		if (!violations.isEmpty()) {
			for (PolicyAssertion.Result violation : violations) {
				errors.rejectValue("password",  "field.policy", violation.getReason());
				errors.rejectValue("passwordConfirm",  "field.policy", violation.getReason());
			}
		}
	}
	
	private boolean passwordFieldChanged(User user) {
		LOG.debug("Checking if password fields changed for user: {}", user.toString());
		return (user.getPassword() != null && !(user.getPassword().isEmpty()) ||
				user.getPasswordConfirm() != null && !(user.getPasswordConfirm().isEmpty()));
	}
}
