package co.insecurity.springref.event.users;

import co.insecurity.springref.event.DeletedEvent;


public class UserDeletedEvent extends DeletedEvent {

	private UserInfo userInfo;
	private boolean deletionAllowed = true;
	
	public UserDeletedEvent(UserInfo userInfo) {
		this.userInfo = userInfo;
	}
	
	public static UserDeletedEvent deletionForbidden(UserInfo userInfo) {
		UserDeletedEvent userDeletedEvent = new UserDeletedEvent(userInfo);
		userDeletedEvent.successful = false;
		userDeletedEvent.deletionAllowed = false;
		return userDeletedEvent;
	}
	
	public static UserDeletedEvent notFound(UserInfo userInfo) {
		UserDeletedEvent userDeletedEvent = new UserDeletedEvent(userInfo);
		userDeletedEvent.successful = false;
		userDeletedEvent.entityFound = false;
		return userDeletedEvent;
	}
	
	public UserInfo getUserInfo() {
		return userInfo;
	}
	
	public boolean isDeletionAllowed() {
		return deletionAllowed;
	}
}