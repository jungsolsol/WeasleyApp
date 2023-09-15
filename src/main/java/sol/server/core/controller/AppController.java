package sol.server.core.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;
import sol.server.common.api.Api;
import sol.server.core.entity.dto.LocAutoRequestDto;
import sol.server.core.entity.dto.LocRequestDto;
import sol.server.core.service.UserLocationService;

@RestController
@RequestMapping("/api/location")
@RequiredArgsConstructor
@Slf4j
public class AppController {

    private final UserLocationService userLocationService;

    @CrossOrigin
    @PostMapping

    public Api<?> getLoc(@RequestBody LocRequestDto locRequestDto) {
        log.info("lat : " + locRequestDto.getLatitude() + " lon : " + locRequestDto.getLongitude());
        return Api.OK("response");
    }


    /**
     * 좌표, 위치이름을 통한 수동 사용자 장소 등록
     *
     * @param dto
     * @return
     */
    @CrossOrigin
    @PostMapping("/manual")
    public Api<?> addLoc(@RequestBody LocRequestDto dto, @AuthenticationPrincipal User user) {
        log.info("dto:" + dto.getLocationName());
        log.info(user.getUsername() + "uuid:" + user.getPassword());

        userLocationService.addManual(dto, user);
        return Api.OK("good");
    }


    /**
     * 위치이름을 통한 자동 장소 등록
     *
     * @param dto
     * @return
     */
    @CrossOrigin
    @PostMapping("/auto")
    public Api<?> autoAddLoc(@RequestBody LocAutoRequestDto dto, @AuthenticationPrincipal User user) {
        userLocationService.addAuto(dto,user);
        return Api.OK("good");
    }
}
