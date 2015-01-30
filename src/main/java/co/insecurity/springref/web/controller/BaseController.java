package co.insecurity.springref.web.controller;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.ModelAttribute;

import co.insecurity.springref.event.users.AllUsersEvent;
import co.insecurity.springref.event.users.RequestAllUsersEvent;
import co.insecurity.springref.event.users.RequestUserInfoEvent;
import co.insecurity.springref.event.users.UserInfo;
import co.insecurity.springref.event.users.UserInfoEvent;
import co.insecurity.springref.persistence.service.UserPersistenceService;
import co.insecurity.springref.web.domain.Alerts;
import co.insecurity.springref.web.domain.User;

abstract class BaseController {

	private static final Logger LOG = LoggerFactory.getLogger(SiteController.class);
	
	@Autowired
	protected UserPersistenceService userService;
	
	@Autowired
	protected PasswordEncoder passwordEncoder;
	
	@ModelAttribute
	protected Alerts getAlerts() {
		return new Alerts();
	}
	
	/**
	 * Retrieve all users from the {@code UserPersistenceService} 
	 * as a list of {@code User} objects
	 * 
	 * @return a list of all users, represented as {@code User} objects
	 */
	protected List<User> getUsers() {
		LOG.debug("Listing users for {}", this.getClass().toString());
		List<User> users = new ArrayList<User>();
		AllUsersEvent allUsersEvent = 
				userService.requestAllUsers(new RequestAllUsersEvent());
		for(UserInfo userInfo : allUsersEvent.getAllUserInfo())
			users.add(User.fromUserInfo(userInfo));
		return users;
	}
	
	/**
	 * Tries to retrieve the user from the {@code UserPersistenceService} with 
	 * the username of the currently authenticated user and returns the user as
	 * a {@code UserInfo} object.
	 * 
	 * If a user with a username matching the current principal cannot be found
	 * (for example if that user does not exist, or if the principal is 
	 * authenticated anonymously), then this method will return null.
	 * 
	 * @return a {@code UserInfo} object representing the current principal, or 
	 * null if the current principal could not be found by the 
	 * {@code UserPersistenceService}
	 */
	protected UserInfo getAuthenticatedUser() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		UserInfoEvent userInfoEvent = userService.requestUserInfo(new RequestUserInfoEvent(auth.getName()));
		if (!userInfoEvent.isEntityFound()) {
			LOG.warn("Could not find user info for user with username: {}", userInfoEvent.getUsername());
			return null;
		}
		else {
			UserInfo userInfo = userInfoEvent.getUserInfo();
			LOG.debug("Retrieved authenticated user: {}", userInfo);
			return userInfo;
		}
	}
	
	/**
	 * Checks whether the current user is authenticated anonymously, meaning 
	 * the that the user has not signed into the application.
	 * 
	 * @return true if the user has not signed in, or false if the user is 
	 * logged in
	 */
	protected boolean isAuthenticatedAnonymously() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		return (auth != null && 
				(auth instanceof AnonymousAuthenticationToken) && 
				auth.isAuthenticated());
	}
}
