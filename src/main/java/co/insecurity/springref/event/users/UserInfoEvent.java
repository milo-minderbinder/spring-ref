package co.insecurity.springref.event.users;

import co.insecurity.springref.event.ReadEvent;

public class UserInfoEvent extends ReadEvent {
	
	private String username;
	private UserInfo userInfo;
	
	
	public UserInfoEvent() { }
	
	public UserInfoEvent(String username, UserInfo userInfo) {
		this.username = username;
		this.userInfo = userInfo;
	}
	
	public static UserInfoEvent notFound(String username) {
		UserInfoEvent event = new UserInfoEvent();
		event.username = username;
		event.entityFound = false;
		return event;
	}
	
	public UserInfo getUserInfo() {
		return userInfo;
	}
	
	public String getUsername() {
		return username;
	}
}