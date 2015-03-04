package co.insecurity.springref.security;

import java.util.Date;
import java.util.UUID;


public class RegistrationAuthorization {
	
	private final UUID uuid;
	private final Date createdDate;
	
	
	public RegistrationAuthorization() {
		uuid = UUID.randomUUID();
		createdDate = new Date();
	}
	
	public static void main(String[] args) {
	}
}
