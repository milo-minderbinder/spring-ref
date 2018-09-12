package co.insecurity.springref.event.users;

import co.insecurity.springref.event.CreatedEvent;

public class UserCreatedEvent extends CreatedEvent {

	private UserInfo userInfo;
	private boolean successful;
	
	public UserCreatedEvent(UserInfo userInfo) {
		this.successful = true;
		this.userInfo = userInfo;
	}
	
	public static UserCreatedEvent usernameAlreadyExists(UserInfo userInfo) {
		UserCreatedEvent userCreatedEvent = new UserCreatedEvent(userInfo);
		userCreatedEvent.successful = false;
		return userCreatedEvent;
	}
	
	public UserInfo getUserInfo() {
		return userInfo;
	}
	
	public boolean isSuccessful() {
		return successful;
	}
}