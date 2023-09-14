package sol.server.core.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import sol.server.core.entity.dto.LocRequestDto;
import sol.server.core.util.UserLocationUtil;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserLocationServiceImpl implements UserLocationService {

    @Override
    public void addManual(LocRequestDto dto, User user){
        UserLocationUtil.addLocationField(dto);
    }
}
