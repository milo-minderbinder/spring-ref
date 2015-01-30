package co.insecurity.springref.event.users;

import co.insecurity.springref.event.CreateEvent;

public class CreateUserEvent extends CreateEvent {
	
	UserInfo userInfo;
	
	public CreateUserEvent(UserInfo userInfo) {
		this.userInfo = userInfo;
	}
	
	public UserInfo getUserInfo() {
		return userInfo;
	}
}