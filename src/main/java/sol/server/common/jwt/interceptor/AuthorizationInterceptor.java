package sol.server.common.jwt.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import sol.server.common.annotation.Jwt;
import sol.server.common.error.ErrorCode;
import sol.server.common.exception.ApiException;

import java.lang.reflect.Method;

@Slf4j
@RequiredArgsConstructor
public class AuthorizationInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info("Authorization Interceptor url : {}", request.getRequestURI());
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();

        if (method.isAnnotationPresent(Jwt.class) || handlerMethod.getBeanType().isAnnotationPresent(Jwt.class)) {

            String token = request.getHeader("Authorization");
            if (!token.isEmpty()) {
                log.info("token : " +token);
            } else {
                throw new ApiException(ErrorCode.BAD_REQUEST,"토큰이 비어있거나 잘못된 형식입니다.");
            }

            //TODO 토큰 검증
//            if (JwtUtil.validateToken(token)) {
//                log.info("validateToken : "+token);
//            } else {
//                throw new ApiException(ErrorCode.BAD_REQUEST, "유효하지 않은 토큰입니다");
//            }
        }
        return true;
    }
}
