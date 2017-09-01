package co.insecurity.springref.config;

import co.insecurity.springref.persistence.repository.UserRepository;
import co.insecurity.springref.persistence.service.UserPersistenceEventHandler;
import co.insecurity.springref.persistence.service.UserPersistenceService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServiceConfig {

    private final UserRepository userRepository;

    public ServiceConfig(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Bean
    public UserPersistenceService userPersistenceService() {
        UserPersistenceService userService = new UserPersistenceEventHandler(userRepository);
        return userService;
    }
}