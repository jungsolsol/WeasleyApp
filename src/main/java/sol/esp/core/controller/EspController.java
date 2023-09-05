package sol.esp.core.controller;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sol.esp.core.dto.EspRequestDto;
import java.time.LocalTime;

@RestController
@Slf4j
public class EspController {


    @GetMapping
    public String getAck(String msg) {
        LocalTime now = LocalTime.now();

        return now.toString();
    }

    @PostMapping("/auth")
    public ResponseEntity<?> authToDevice(@RequestBody EspRequestDto dto) {
        LocalTime now = LocalTime.now();
        log.info(now.toString());
        System.out.println(dto.getApi_key());
        return ResponseEntity.ok(dto.getApi_key());
    }

}
