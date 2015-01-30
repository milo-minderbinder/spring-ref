package co.insecurity.springref.event.users;

import co.insecurity.springref.event.UpdateEvent;

public class UpdateUserEvent extends UpdateEvent {
	
	private UserInfo userInfo;
	
	public UpdateUserEvent(UserInfo userInfo) {
		this.userInfo = userInfo;
	}

	public UserInfo getUserInfo() {
		return userInfo;
	}
}