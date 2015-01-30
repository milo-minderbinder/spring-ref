package co.insecurity.springref.event.users;

public class RequestUserInfoEvent {
	private String username;
	
	public RequestUserInfoEvent(String username) {
		this.username = username;
	}
	
	public String getUsername() {
		return username;
	}
}