package co.insecurity.springref.persistence.repository;

import co.insecurity.springref.persistence.domain.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
//import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface UserRepository extends CrudRepository<User, String> {
    User findByUsername(String username);
}