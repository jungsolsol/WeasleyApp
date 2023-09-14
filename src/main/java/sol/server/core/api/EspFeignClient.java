package sol.server.core.api;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "esp-api", url = "http://localhost:8082")
public interface EspFeignClient {

}
