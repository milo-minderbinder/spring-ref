package co.insecurity.springref.persistence.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import co.insecurity.springref.persistence.domain.User;

@Repository
public interface UserRepository extends CrudRepository<User, String> {
	User findByUsername(String username);
}