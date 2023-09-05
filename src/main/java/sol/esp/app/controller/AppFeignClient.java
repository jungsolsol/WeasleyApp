package sol.esp.app.controller;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import sol.esp.core.dto.EspRequestDto;

@FeignClient(name = "app-api", url = "http://localhost:8080")
public interface AppFeignClient {
    @PostMapping("/api/auth")
    ResponseEntity<?> auth(@RequestBody EspRequestDto dto);

}
