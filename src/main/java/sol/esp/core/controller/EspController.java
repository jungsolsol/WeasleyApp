package sol.esp.core.controller;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sol.esp.app.controller.AppFeignClient;
import sol.esp.core.dto.EspRequestDto;
import java.time.LocalTime;

@RestController
@Slf4j
@RequiredArgsConstructor
public class EspController {


    private final AppFeignClient appFeignClient;
    @GetMapping
    public String getAck(String msg) {
        LocalTime now = LocalTime.now();

        return now.toString();
    }

    @PostMapping("/auth")
    public ResponseEntity<?> authToDevice(@RequestBody EspRequestDto dto) {
        appFeignClient.auth(dto);
        return ResponseEntity.ok(dto.getApi_key());
    }

}
