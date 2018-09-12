package co.insecurity.springref.event.users;

import java.util.List;

import co.insecurity.springref.event.ReadEvent;

public class AllUsersEvent extends ReadEvent {
	
	private List<UserInfo> allUserInfo;
	
	public AllUsersEvent(List<UserInfo> allUserInfo) {
		this.allUserInfo = allUserInfo;
	}
	
	public List<UserInfo> getAllUserInfo() {
		return allUserInfo;
	}
}