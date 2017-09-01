package co.insecurity.springref.core.domain;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

public enum UserRole {
	ANONYMOUS("ANONYMOUS"),
	USER("USER"),
	ADMIN("ADMIN");
	
	private final String roleName;
	
	UserRole(String roleName) {
		this.roleName = roleName;
	}
	
	/**
	 * Constructs a {@code SimpleGrantedAuthority} representing this 
	 * {@code UserRole}.
	 * 
	 * Note that the {@code SimpleGrantedAuthority} is constructed with 
	 * the String representation of this {@code UserRole} prefixed by  
	 * the String {@code "ROLE_"}.
	 * 
	 * @return the {@code SimpleGrantedAuthority} representing this 
	 * {@code UserRole}
	 */
	public SimpleGrantedAuthority toSimpleGrantedAuthority() {
		return new SimpleGrantedAuthority("ROLE_" + roleName);
	}
	
	@Override
	public String toString() { 
		return roleName; 
	}
}