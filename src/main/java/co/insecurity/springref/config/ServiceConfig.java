package co.insecurity.springref.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import co.insecurity.springref.persistence.repository.UserRepository;
import co.insecurity.springref.persistence.service.UserPersistenceEventHandler;
import co.insecurity.springref.persistence.service.UserPersistenceService;

@Configuration
public class ServiceConfig {

	@Bean
	public UserPersistenceService userPersistenceService(UserRepository userRepository) {
		UserPersistenceService userService = new UserPersistenceEventHandler(userRepository);
		return userService;
	}
}