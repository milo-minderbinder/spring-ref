package co.insecurity.springref.web.domain;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.BeanUtils;

import co.insecurity.springref.core.domain.UserRole;
import co.insecurity.springref.event.users.UserInfo;

public class User {

	private String username;
	private String password;
	private String passwordConfirm;
	private Set<UserRole> roles;
	private String firstName;
	private String lastName;
	
	public User() {
		this(null, null, new HashSet<UserRole>(), null, null);
	}
	
	public User(String username, String password, Set<UserRole> roles, String firstName, String lastName) {
		this.username = username;
		this.password = password;
		this.firstName = firstName;
		this.lastName = lastName;
		this.roles = roles;
	}
	
	/**
	 * Return a new {@code User} from the supplied {@code UserInfo}. Password 
	 * fields are not copied.
	 * 
	 * @param userInfo the {@code UserInfo} object to build the {@code User} from
	 * @return a new {@code User} object based on the supplied {@code UserInfo} object
	 * @throws IllegalArgumentException if {@code UserInfo} is null
	 */
	public static User fromUserInfo(UserInfo userInfo) {
		if (userInfo == null)
			throw new IllegalArgumentException("UserInfo cannot be null!");
		User user = new User();
		BeanUtils.copyProperties(userInfo, user);
		user.setPassword(null);
		user.setPasswordConfirm(null);
		return user;
	}
	
	public UserInfo toUserInfo() {
		UserInfo userInfo = new UserInfo();
		BeanUtils.copyProperties(this, userInfo);
		return userInfo;
	}
	
	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getPasswordConfirm() {
		return passwordConfirm;
	}

	public void setPasswordConfirm(String passwordConfirm) {
		this.passwordConfirm = passwordConfirm;
	}
	
	public Set<UserRole> getRoles() {
		return roles;
	}
	
	public void setRoles(Set<UserRole> roles) {
		this.roles = roles;
	}

	public String getFirstName() {
		return firstName;
	}
	
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	
	public String getLastName() {
		return lastName;
	}
	
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
	@Override
	public String toString() {
		return String.format("User[username=%s, first=%s, last=%s]",
				username, firstName, lastName);
	}
}