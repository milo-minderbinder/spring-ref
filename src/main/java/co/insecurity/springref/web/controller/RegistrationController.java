package co.insecurity.springref.web.controller;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import co.insecurity.springref.event.users.CreateUserEvent;
import co.insecurity.springref.event.users.UserCreatedEvent;
import co.insecurity.springref.event.users.UserInfo;
import co.insecurity.springref.web.domain.Alerts;
import co.insecurity.springref.web.domain.User;
import co.insecurity.springref.web.domain.UserValidator;


@Controller
@RequestMapping(value="/register")
public class RegistrationController extends BaseController {

	private static final Logger LOG = 
			LoggerFactory.getLogger(RegistrationController.class);

	
	@Autowired
	private UserValidator userValidator;
	
	@RequestMapping(method=RequestMethod.GET)
	public String register() {
		LOG.info("In register() method.");
		return "/register";
	}
	
	@RequestMapping(method=RequestMethod.POST)
	public String registerUser(@ModelAttribute Alerts alerts,
			@Valid @ModelAttribute User user,
			BindingResult result, RedirectAttributes redirectAttrs, Model model) {
		LOG.debug("In registerUser() method with user: '{}'", user.toString());
		userValidator.validate(user, result);
		if (result.hasErrors()) {
			LOG.debug("Registration unsuccessful: validation failed.");
			alerts.addAlert("Please fix the errors in the registration form and resubmit.",
					Alerts.AlertType.DANGER);
			return "/register";
		}
		// Valid registration - convert to UserInfo, encode password, and persist
		UserInfo userInfo = user.toUserInfo();
		userInfo.setPassword(passwordEncoder.encode(userInfo.getPassword()));
		UserCreatedEvent userCreatedEvent = userService
				.createUser(new CreateUserEvent(userInfo));
		if (!userCreatedEvent.isSuccessful()) {
			LOG.debug("Registration unsuccessful: username already exists.");
			alerts.addAlert("Sorry! That username is not available.", Alerts.AlertType.WARNING);
			result.rejectValue("username", "error.user", "That username already exists.");
			return "/register";
		} else {
			LOG.debug("User successfully created: {}", user.getUsername());
			alerts.addAlert("Registration successful!", Alerts.AlertType.SUCCESS);
			redirectAttrs.addFlashAttribute(alerts);
			return "redirect:/";
		}
	}
	

	@ModelAttribute("user")
	private User getUser() {
		return new User();
	}
}