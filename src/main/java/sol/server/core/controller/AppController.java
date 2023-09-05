package sol.server.core.controller;

import org.springframework.web.bind.annotation.*;
import sol.server.common.annotation.User;
import sol.server.common.api.Api;
import sol.server.core.entity.dto.LocRequestDto;

@RestController
@RequestMapping("/api/location")
public class AppController {

    @CrossOrigin
    @PostMapping
    public Api<?> getLoc(@RequestBody LocRequestDto locRequestDto) {
        System.out.println(locRequestDto.getLatitude() + "" + locRequestDto.getLongitude());

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
    public Api<?> addLoc(@RequestBody LocRequestDto dto, @User String userKey) {
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
    public Api<?> autoAddLoc(@RequestBody LocRequestDto dto) {

        return Api.OK("good");
    }
}
