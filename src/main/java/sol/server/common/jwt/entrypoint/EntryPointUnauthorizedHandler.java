package sol.server.common.jwt.entrypoint;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import sol.server.common.api.Api;
import sol.server.common.api.Result;
import sol.server.common.error.TokenCode;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class EntryPointUnauthorizedHandler implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;


    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        log.warn("UNAUTHORIZED ACCESS");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");

        // 401 에러 응답을 생성하여 클라이언트에 반환
        Api<?> apiResponse = Api.ERROR(TokenCode.EXPIRED_TOKEN);
        apiResponse.setResult(Result.ERROR(TokenCode.AUTHORIZATION_TOKEN_NOT_FOUND,authException));
        String jsonResponse = objectMapper.writeValueAsString(apiResponse); // yourObjectMapper는 사용 중인 ObjectMapper 객체를 참조해야 합니다.

        response.getWriter().write(jsonResponse);
    }
}
