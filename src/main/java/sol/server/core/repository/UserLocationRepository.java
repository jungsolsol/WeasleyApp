package sol.server.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sol.server.core.entity.User;
import sol.server.core.entity.UserLocation;

import java.util.List;
import java.util.Optional;

public interface UserLocationRepository extends JpaRepository<UserLocation, Long> {


    Optional<List<UserLocation>> findAllByUserAndLocationType(User user, String locationType);
}
