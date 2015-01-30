package co.insecurity.springref.event.users;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import co.insecurity.springref.core.domain.UserRole;

public class UserInfo implements Serializable {

	private static final long serialVersionUID = -9108173233963629910L;

	private String username;
	private String password;
	private String firstName;
	private String lastName;
	private Set<UserRole> roles;
	
	
	public UserInfo() { 
		this(null, null);
	}
	
	public UserInfo(String username, String password) {
		this(username, password, new HashSet<UserRole>(), null, null);
	}
	
	public UserInfo(String username, String password, Set<UserRole> roles, 
			String firstName, String lastName) {
		this.username = username;
		this.password = password;
		this.roles = roles;
		this.firstName = firstName;
		this.lastName = lastName;
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
	
	public Set<UserRole> getRoles() {
		return roles;
	}
	
	public void setRoles(Set<UserRole> roles) {
		this.roles = roles;
	}
	
	@Override
	public String toString() {
		return String.format("UserInfo[username=%s, roles=%s, first=%s, last=%s",
				username, roles.toString(), firstName, lastName);
	}
}