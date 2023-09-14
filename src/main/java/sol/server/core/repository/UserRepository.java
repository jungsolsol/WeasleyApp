package sol.server.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sol.server.core.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {


    Optional<User> findByUserName(String username);
}
