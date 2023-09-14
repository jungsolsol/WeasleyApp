package sol.server.common.auth.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;
import sol.server.common.auth.dto.AppRequestDto;
import sol.server.common.auth.dto.EspRequestDto;
import sol.server.common.auth.service.AuthService;
import sol.server.common.annotation.Jwt;
import sol.server.common.api.Api;
import sol.server.common.jwt.Token;
import sol.server.common.jwt.util.JwtUtil;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;

    @GetMapping("/auth")
    public Api<?> authPage(HttpServletRequest request, @RequestParam String username) {

        // refresh-token만료시 로그인

        return Api.OK("토큰 갱신");
    }


    @PostMapping("/auth-a")
    public Api<?> authByApp(@RequestBody AppRequestDto dto) {
        log.info("Auth by Application Start....");
        Token token = authService.saveUserInfoAndProductUUID(dto);

        System.out.println(token.getAccessToken());

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add("Authorization", "Bearer "+token.getAccessToken());
        responseHeaders.add("Authorization", "Refresh "+token.getRefreshToken());
        Api<?> apiResponse = Api.OK("유저 인증 완료");
        apiResponse.addHeaders(responseHeaders);
        return apiResponse;
    }

    @PostMapping("/auth-e")
    public Api<?> authByEsp(@RequestBody EspRequestDto dto) {
        log.info("Auth by Esp Start....");
        authService.saveProductUUID(dto);
        return Api.OK("기기 인증 완료");
    }

    @Jwt
    @PostMapping("test")
    public Api<?> authTest() {
        return Api.OK("TEST");
    }
}
