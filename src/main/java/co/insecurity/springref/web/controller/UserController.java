package co.insecurity.springref.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ValidationUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import co.insecurity.springref.event.users.UpdateUserEvent;
import co.insecurity.springref.event.users.UserInfo;
import co.insecurity.springref.event.users.UserUpdatedEvent;
import co.insecurity.springref.web.domain.Alerts;
import co.insecurity.springref.web.domain.User;
import co.insecurity.springref.web.domain.UserValidator;


@Controller
@RequestMapping(value="/user")
public class UserController extends BaseController {

	private static final Logger LOG = 
			LoggerFactory.getLogger(UserController.class);

	@Autowired
	private UserValidator userValidator;
	
	@RequestMapping(value="viewProfile", method=RequestMethod.GET)
	public String viewProfile(@ModelAttribute User user) {
		LOG.info("In viewProfile() view with User: {}", user.toString());
		return "/profile";
	}
	
	@RequestMapping(value="editProfile", method=RequestMethod.POST)
	public String editProfile(@ModelAttribute("alerts") Alerts alerts,
			@ModelAttribute("user") User user, 
			BindingResult result, RedirectAttributes redirectAttrs, Model model) {
		LOG.debug("In editProfile() view with user: '{}'", user.toString());
		boolean passwordChanged = passwordFieldChanged(user);
		userValidator.validate(user, result);
		if (result.hasErrors()) {
			LOG.debug("Edit profile unsuccessful: validation failed.");
			alerts.addAlert("Please fix the errors in the form and resubmit.",
					Alerts.AlertType.DANGER);
			return "/profile";
		} else {
			if (passwordChanged) {
				redirectAttrs.addFlashAttribute("updatedUser", user);
				LOG.debug("Redirecting to confirm updated User: {}", user);
				return "redirect:/user/editProfile/confirm";
			}
			else {
				user.setPassword(getAuthenticatedUser().getPassword());
			}
			return attemptUserUpdate(user, alerts, redirectAttrs, model);
		}
	}
	
	@RequestMapping(value="editProfile/confirm", method=RequestMethod.GET)
	public String viewEditProfileConfirm(@ModelAttribute("updatedUser") User updatedUser) {
		LOG.debug(String.format(
				"In doEditProfileConfirm() view with updatedUserInfo: %s", 
				updatedUser.toString()));
		return "/confirmChange";
	}
	
	@RequestMapping(value="editProfile/confirm", method=RequestMethod.POST)
	public String doEditProfileConfirm(@RequestParam(required=true) String currentPassword, 
			@ModelAttribute Alerts alerts, @ModelAttribute("updatedUser") User updatedUser, 
			BindingResult result, RedirectAttributes redirectAttrs, Model model) {
		LOG.debug(String.format(
				"In doEditProfileConfirm() view with updatedUser: %s", 
				updatedUser.toString()));
		ValidationUtils.invokeValidator(userValidator, updatedUser, result);
		if (result.hasErrors()) {
			alerts.addAlert("Please fix the errors in the form and resubmit.",
					Alerts.AlertType.DANGER);
			return "/profile";
		}
		if (!(passwordEncoder.matches(
				currentPassword, 
				getAuthenticatedUser().getPassword()))) 
		{
			LOG.debug("Edit profile unsuccessful: provided incorrect current password!");
			alerts.addAlert("Please provide your current password to confirm your profile update.",
					Alerts.AlertType.WARNING);
			//result.rejectValue("currentPassword", "error.user", "Incorrect password!");
			return "confirmChange";
		} else 
		{
			LOG.debug("Updating user profile with new user info: {}", updatedUser.toString());
			updatedUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
			return attemptUserUpdate(updatedUser, alerts, redirectAttrs, model);
		}
	}

	@ModelAttribute("user")
	private User getUser() {
		return User.fromUserInfo(getAuthenticatedUser());
	}
	
	private String attemptUserUpdate(@ModelAttribute User updatedUser, 
			@ModelAttribute Alerts alerts,
			RedirectAttributes redirectAttrs, Model model) {
		LOG.debug("Attempting user update for user: {}", updatedUser.toString());
		// (Re)copy immutable user attributes into updatedUser to prevent manipulation
		UserInfo authenticatedUser = getAuthenticatedUser();
		updatedUser.setUsername(authenticatedUser.getUsername());
		updatedUser.setRoles(authenticatedUser.getRoles());
		UserUpdatedEvent userUpdatedEvent = 
				userService.updateUser(new UpdateUserEvent(updatedUser.toUserInfo()));
		if (!userUpdatedEvent.isSuccessful()) {
				LOG.warn("Update profile unsuccessful for user: {}", 
						userUpdatedEvent.getUserInfo());
				alerts.addAlert(String.format(
						"User profile update unsuccessful for user: %s", 
								userUpdatedEvent.getUserInfo()),
						Alerts.AlertType.DANGER);
				return "profile";
		} else {
			LOG.debug("Successfully updated user: {}", userUpdatedEvent.getUserInfo());
			alerts.addAlert("Profile updated successfully!", Alerts.AlertType.SUCCESS);
			redirectAttrs.addFlashAttribute(alerts);
			return "redirect:/user/viewProfile";
		}
	}
	
	private boolean passwordFieldChanged(User user) {
		return (user.getPassword() != null && !(user.getPassword().isEmpty()) ||
				user.getPasswordConfirm() != null && !(user.getPasswordConfirm().isEmpty()));
	}
}