package co.insecurity.springref.persistence.service;

import co.insecurity.springref.event.users.*;
import co.insecurity.springref.persistence.domain.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
public interface UserPersistenceService extends UserDetailsService {

    UserCreatedEvent createUser(CreateUserEvent createUserEvent);

    UserDeletedEvent deleteUser(DeleteUserEvent deleteUserEvent);

    UserUpdatedEvent updateUser(UpdateUserEvent updateUserEvent);

    UserInfoEvent requestUserInfo(RequestUserInfoEvent requestUserDetailsEvent);

    AllUsersEvent requestAllUsers(RequestAllUsersEvent requestAllUsersEvent);

    User loadUserByUsername(String username) throws UsernameNotFoundException;
}