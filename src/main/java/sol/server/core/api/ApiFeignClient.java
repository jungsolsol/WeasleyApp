package sol.server.core.api;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import sol.server.common.api.Api;
import sol.server.core.entity.dto.LocRequestDto;

@FeignClient(name = "map-api", url = "http://localhost:8081")
public interface ApiFeignClient {

    @PostMapping("/api/auto")
    ResponseEntity<Api> autoLocation(String userName);

}
