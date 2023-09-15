package sol.server.core.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import sol.server.core.api.ApiFeignClient;
import sol.server.core.entity.UserLocation;
import sol.server.core.entity.dto.LocAutoRequestDto;
import sol.server.core.entity.dto.LocRequestDto;
import sol.server.core.repository.UserLocationRepository;
import sol.server.core.repository.UserRepository;
import sol.server.core.util.UserLocationUtil;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserLocationServiceImpl implements UserLocationService {

    private final ApiFeignClient apiFeignClient;
    private final UserRepository userRepository;
    private final UserLocationRepository userLocationRepository;
    @Override
    public void addManual(LocRequestDto dto, User user){
        sol.server.core.entity.User findUser = userRepository.findByUserName(user.getUsername()).orElseThrow(()
                -> new UsernameNotFoundException("Member Not found"));
        String locationType = "M"; //manual
        UserLocation userLocation = UserLocationUtil.addLocationField(dto, findUser, locationType);
        userLocationRepository.save(userLocation);
    }



    @Override
    public void addAuto(LocAutoRequestDto dto, User user) {
        sol.server.core.entity.User findUser = userRepository.findByUserName(user.getUsername()).orElseThrow(()
                -> new UsernameNotFoundException("Member Not found"));
        dto.getLocationName().forEach(value -> log.info(value));
        userLocationRepository.findAllByUserAndLocationType(findUser, "M");
        apiFeignClient.autoLocation(user.getUsername());

    }


}
