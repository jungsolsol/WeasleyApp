package sol.server.core.service;

import org.springframework.security.core.userdetails.User;
import sol.server.core.entity.dto.LocRequestDto;

public interface UserLocationService {
    void addManual(LocRequestDto dto, User user);
}
