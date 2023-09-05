package sol.server.common.jwt.resolver;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import sol.server.common.annotation.User;

@Slf4j
public class AuthorizationHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        log.info("supportParameter");
        return methodParameter.hasParameterAnnotation(User.class);
    }
    @Override
    public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer, NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) throws Exception {

        String token = nativeWebRequest.getHeader("Authorization");

        //TODO String userKey = JwtUtil.getUserKeyFromToken(token); 구현
//        String userKey = JwtUtil.getUsernameFromToken(token);
////        log.info("resolveArgument: " + userKey);
////        if (!userKey.isEmpty()) {
////            log.info(userKey);
////            return userKey;
////        } else {
////            throw new NoUserException();
////        }
//    }
        return null;
    }
}
