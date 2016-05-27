package co.insecurity.springref.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import co.insecurity.springref.web.domain.Alerts;

import java.util.ArrayList;
import java.util.List;


@Controller
public class SiteController extends BaseController {

	private static final Logger LOG = LoggerFactory.getLogger(SiteController.class);

	@Autowired
	private SessionRegistry sessionRegistry;

	private List<SessionInformation> getActiveSessions() {
		List<SessionInformation> activeSessions = new ArrayList<>();
		for (Object principal : sessionRegistry.getAllPrincipals()) {
			activeSessions.addAll(sessionRegistry.getAllSessions(principal, false));
		}
		return activeSessions;
	}

	@RequestMapping(value="/", method=RequestMethod.GET)
	public String home(Model model) {
		LOG.debug("in home() view");
		model.addAttribute("userList", getUsers());
		return "/home";
	}
	
	@RequestMapping(value="/login", method=RequestMethod.GET)
	public String login(
			@RequestParam(value="error", required=false) String error,
			@RequestParam(value="logout", required=false) String logout,
			@ModelAttribute Alerts alerts,
			RedirectAttributes redirectAttrs,
			Model model) {
		LOG.debug("in login() view");
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null) {
			alerts.addAlert("isAuthenticated: " + auth.isAuthenticated());
			alerts.addAlert("auth: " + auth);
			alerts.addAlert("principle: " + auth.getPrincipal());
			alerts.addAlert("creds: " + auth.getCredentials());
			alerts.addAlert("details: " + auth.getDetails());
			LOG.info("SessionRegistry in login(): " + sessionRegistry.toString());
			for (SessionInformation si : getActiveSessions()) {
				alerts.addAlert("Session: " + si.getPrincipal() + " | " + si.getSessionId());
			}
		}
		
		if (error != null)
			alerts.addAlert("Invalid username or password!",
					Alerts.AlertType.DANGER);
		if (logout != null) {
			if (!(isAuthenticatedAnonymously())) {
				alerts.addAlert("You have not been logged out!",
						Alerts.AlertType.WARNING);
				redirectAttrs.addFlashAttribute(alerts);
				return "redirect:/";
			}
			else
				alerts.addAlert("You have been successfully logged out!",
						Alerts.AlertType.SUCCESS);
		}
		return "/login";
	}
}