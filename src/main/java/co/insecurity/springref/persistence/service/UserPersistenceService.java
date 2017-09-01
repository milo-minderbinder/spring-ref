package co.insecurity.springref.persistence.service;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import co.insecurity.springref.persistence.domain.User;
import co.insecurity.springref.event.users.*;


@Service
public interface UserPersistenceService extends UserDetailsService {

	public UserCreatedEvent createUser(CreateUserEvent createUserEvent);
	public UserDeletedEvent deleteUser(DeleteUserEvent deleteUserEvent);
	public UserUpdatedEvent updateUser(UpdateUserEvent updateUserEvent);
	public UserInfoEvent requestUserInfo(RequestUserInfoEvent requestUserDetailsEvent);
	public AllUsersEvent requestAllUsers(RequestAllUsersEvent requestAllUsersEvent);
	public User loadUserByUsername(String username) throws UsernameNotFoundException;
}