package sol.server.common.jwt.filter;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import sol.server.common.error.TokenCode;
import sol.server.common.jwt.util.JwtUtil;
import sol.server.core.repository.ProductRepository;

import java.io.IOException;
import java.util.Collections;


@RequiredArgsConstructor
@Slf4j

public class JwtTokenFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("Token filter Start .... ");
        String accessToken = resolveToken(request, "Authorization");
        String userName = null;

        if (jwtUtil.validateToken(accessToken) == TokenCode.ACCESS_TOKEN) {
            Authentication authentication = jwtUtil.getAuthentication(accessToken);
            SecurityContextHolder.getContext().setAuthentication(authentication); // security context에 인증 정보 저장
        } else if (jwtUtil.validateToken(accessToken) == TokenCode.EXPIRED_TOKEN) {

            if (StringUtils.hasText(request.getHeader("RefreshToken"))) { // Auth에는 userName 담겨 있음

                String token = request.getHeader("RefreshToken");
                log.info("refreshToken:"+token);

                if (jwtUtil.validateToken(token) == TokenCode.ACCESS_TOKEN) {
                    log.info("Reissue access token");
                    userName = jwtUtil.extractAllClaims(token);
                    log.info(userName);
                    String refreshToken = jwtUtil.getRefreshToken(userName); // userId로 refreshToken 조회
                    log.info(refreshToken);
//                if (jwtUtil.validateToken(refreshToken) == TokenCode.ACCESS_TOKEN) {
                    Authentication authentication = jwtUtil.getAuthentication(refreshToken);
                    String newAccessToken = jwtUtil.generateAccessToken(authentication);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    response.setHeader(HttpHeaders.AUTHORIZATION, newAccessToken);

                } else {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write("Token expired");
                }
//                userName = jwtUtil.extractAllClaims(token);
//
//                log.info(userName);
//                String refreshToken = jwtUtil.getRefreshToken(userName); // userId로 refreshToken 조회
//                log.info(refreshToken);
////                if (jwtUtil.validateToken(refreshToken) == TokenCode.ACCESS_TOKEN) {
//                    Authentication authentication = jwtUtil.getAuthentication(refreshToken);
//                    String newAccessToken = jwtUtil.generateAccessToken(authentication);
//                    SecurityContextHolder.getContext().setAuthentication(authentication);
////
//                    response.setHeader(HttpHeaders.AUTHORIZATION, newAccessToken);
//                    log.info("Reissue access token");
//                } else {
//                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//                    response.getWriter().write("Token expired");
//                }
            }
        }
        log.info("next Filter");
        filterChain.doFilter(request, response);
    }

    public String resolveToken(HttpServletRequest request, String header) {
        String bearerToken = request.getHeader(header);
        log.info(bearerToken);

        if (StringUtils.hasText(bearerToken)) {
            return bearerToken;
        }
        return null;
    }





}
