package co.insecurity.springref.event.users;

import co.insecurity.springref.event.DeleteEvent;


public class DeleteUserEvent extends DeleteEvent {
	
	UserInfo userInfo;
	
	public DeleteUserEvent(UserInfo userInfo) {
		this.userInfo = userInfo;
	}

	public UserInfo getUserInfo() {
		return userInfo;
	}
}