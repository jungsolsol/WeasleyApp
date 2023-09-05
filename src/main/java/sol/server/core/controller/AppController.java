package sol.server.core.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import sol.server.core.entity.LocRequestDto;

@RestController
public class AppController {

    @CrossOrigin
    @PostMapping("/loc")
    public ResponseEntity<?> getLoc(@RequestBody LocRequestDto locRequestDto) {
        System.out.println(locRequestDto.getLatitude()+ "" + locRequestDto.getLongitude());

        return ResponseEntity.ok("good");
    }
}
