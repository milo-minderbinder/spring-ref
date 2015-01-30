package co.insecurity.springref.event.users;

import co.insecurity.springref.event.UpdatedEvent;

public class UserUpdatedEvent extends UpdatedEvent {

	private UserInfo userInfo;
	private boolean updateAllowed = true;
	
	public UserUpdatedEvent(UserInfo userInfo) {
		this.userInfo = userInfo;
	}
	
	public static UserUpdatedEvent updateForbidden(UserInfo userInfo) {
		UserUpdatedEvent updatedEvent = new UserUpdatedEvent(userInfo);
		updatedEvent.successful = false;
		updatedEvent.updateAllowed = false;
		return updatedEvent;
	}
	
	public static UserUpdatedEvent notFound(UserInfo userInfo) {
		UserUpdatedEvent updatedEvent = new UserUpdatedEvent(userInfo);
		updatedEvent.successful = false;
		updatedEvent.entityFound = false;
		return updatedEvent;
	}
	
	public UserInfo getUserInfo() {
		return userInfo;
	}
	
	public boolean isSuccessful() {
		return successful;
	}
	
	public boolean isUpdateAllowed() {
		return updateAllowed;
	}
}