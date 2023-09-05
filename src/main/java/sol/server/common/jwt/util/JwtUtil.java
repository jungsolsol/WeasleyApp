package sol.server.common.jwt.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import sol.server.common.error.TokenErrorCode;
import sol.server.common.exception.ApiException;

import java.security.SignatureException;
import java.util.Date;

@Slf4j
public class JwtUtil {
    private static final String SECRET_KEY =
            "solsolsolsolsolsolsolsosloslsolsosloslosloslsolsolsososloslsolsoslssoolsolsolsolsosolsosloslosloslsoloslsolsolsoslosl"; // 비밀 키 (랜덤하고 보안 강화를 위해 변경하세요)

    public static String generateToken(String username) {

        //1시간
        long expirationTimeInMillis = 3600000;

        //7일
        long refreshTokenExpirationTimeInMillis = 604800000;

        //RefreshToken 생성
        String refreshToken = Jwts.builder()
                .setSubject(username)
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenExpirationTimeInMillis))
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                .compact();

        //AccessToken 생성
        String accessToken = Jwts.builder()
                .setSubject(username)
                .setExpiration(new Date(System.currentTimeMillis() + expirationTimeInMillis))
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                .compact();


        return accessToken;
    }

    public static boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static String getUsernameFromToken(String token) {
        Claims claims = Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
        return claims.getSubject();
    }


    public static String validateBearerToken(String authorizationHeader) throws ApiException {

        String userKey = "";
        if (StringUtils.hasText(authorizationHeader) && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);
            try {
                JwtUtil.validateToken(token);
                log.info(token);
                userKey = JwtUtil.getUsernameFromToken(token);
                log.info(userKey);
                return userKey;
            } catch (Exception e) {
                if (e instanceof SignatureException) {
                    throw new ApiException(TokenErrorCode.INVALID_TOKEN, e);
                } else if (e instanceof ExpiredJwtException) {
                    throw new ApiException(TokenErrorCode.EXPIRED_TOKEN, e);
                } else {
                    throw new ApiException(TokenErrorCode.TOKEN_EXCEPTION, e);
                }
            }
        } else {
            throw new ApiException(TokenErrorCode.INVALID_TOKEN);
        }

    }

}