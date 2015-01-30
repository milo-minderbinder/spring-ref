package co.insecurity.springref.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ValidationUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import co.insecurity.springref.event.users.*;
import co.insecurity.springref.web.domain.Alerts;
import co.insecurity.springref.web.domain.User;
import co.insecurity.springref.web.domain.UserValidator;


@Controller
@RequestMapping(value="/admin")
public class AdminController extends BaseController {

	private static final Logger LOG = LoggerFactory.getLogger(AdminController.class);
	
	
	@RequestMapping(method=RequestMethod.GET)
	public String admin(Model model) {
		LOG.debug("in admin() method");
		model.addAttribute("userList", getUsers());
		return "/admin";
	}

	@RequestMapping(value="/deleteUser", method=RequestMethod.POST)
	public String deleteUser(@ModelAttribute User user, 
			@ModelAttribute Alerts alerts,
			RedirectAttributes redirectAttrs, Model model) {
		LOG.debug("in deleteUser() method with user: '{}'", user.toString());
		UserDeletedEvent userDeletedEvent = userService
				.deleteUser(new DeleteUserEvent(user.toUserInfo()));
		if (!userDeletedEvent.isSuccessful()) {
			LOG.debug("Failed to delete user: '{}'", user.toString());
			alerts.addAlert(String.format(
					"User deletion failed for user: %s", user.toString()),
					Alerts.AlertType.DANGER);
			return "/admin";
		} else {
			LOG.debug("Successfully deleted user: '{}'", user.toString());
					alerts.addAlert(String.format(
							"Successfully deleted user: %s", user.toString()),
							Alerts.AlertType.SUCCESS);
					redirectAttrs.addFlashAttribute(alerts);
		}
		return "redirect:/admin";
	}
	
	@RequestMapping(value="/editUser", method=RequestMethod.GET)
	public String viewEditUser(@RequestParam String username, @ModelAttribute Alerts alerts, 
			Model model) {
		LOG.debug("In editUser() view with username: {}", username); 
		UserInfoEvent userInfoEvent = 
				userService.requestUserInfo(new RequestUserInfoEvent(username));
		if (!(userInfoEvent.isEntityFound())) {
			LOG.debug("Could not find user: {}", username);
			alerts.addAlert(String.format(
					"Could not find user with username: '%s'", username),
					Alerts.AlertType.WARNING);
			return "/admin";
		} else {
			UserInfo userInfo = userInfoEvent.getUserInfo();
			LOG.debug("Retrieved UserInfo: {}", userInfo);
			model.addAttribute("user", User.fromUserInfo(userInfo));
			return "/editUser";
		}
	}
	
	@RequestMapping(value="/editUser", method=RequestMethod.POST)
	public String doEditUser(@ModelAttribute("user") User user, @ModelAttribute Alerts alerts,
			BindingResult result, RedirectAttributes redirectAttrs, Model model) {
		LOG.debug(String.format(
				"In doEditUser() view with user: %s", 
				user.toString()));
		ValidationUtils.invokeValidator(new UserValidator(), user, result);
		if (result.hasErrors()) {
			alerts.addAlert("Please fix the errors in the form and resubmit.",
					Alerts.AlertType.DANGER);
			return "/admin/editUser";
		}
		else 
		{	
			LOG.debug("Updating user with new user info: {}", user.toString());
			String username = user.getUsername();
			UserInfoEvent userInfoEvent = 
					userService.requestUserInfo(new RequestUserInfoEvent(username));
			if (!(userInfoEvent.isEntityFound())) {
				LOG.debug("Could not find user: {}", username);
				alerts.addAlert(String.format(
						"Could not find user with username: '%s'", username),
						Alerts.AlertType.WARNING);
				redirectAttrs.addFlashAttribute("alerts", alerts);
				return "redirect:/admin";
			}
			UserInfo userInfo = userInfoEvent.getUserInfo();
			LOG.debug("Retrieved UserInfo: {}", userInfo);
			// Set unmodifiable user properties
			user.setPassword(userInfo.getPassword());
			user.setRoles(userInfo.getRoles());
			UserUpdatedEvent userUpdatedEvent = 
					userService.updateUser(new UpdateUserEvent(user.toUserInfo()));
			if (!userUpdatedEvent.isSuccessful()) {
					LOG.warn("Update profile unsuccessful for user: {}", 
							userUpdatedEvent.getUserInfo());
					alerts.addAlert(String.format(
							"User update unsuccessful for user: %s", 
									userUpdatedEvent.getUserInfo()),
							Alerts.AlertType.DANGER);
					redirectAttrs.addFlashAttribute("alerts", alerts);
					return "redirect:/admin";
			} else {
				LOG.debug("Successfully updated user: {}", userUpdatedEvent.getUserInfo());
				alerts.addAlert("Profile updated successfully!", Alerts.AlertType.SUCCESS);
				redirectAttrs.addFlashAttribute(alerts);
				return "redirect:/admin";
			}
		}
	}
}