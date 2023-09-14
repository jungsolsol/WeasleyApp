package sol.server.common.jwt.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import sol.server.common.error.TokenCode;
import sol.server.common.jwt.Token;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtUtil {

    private final RedisTemplate redisTemplate;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    private final long accessTokenValidTime = (60 * 1000) * 60;// 60분
    private final long refreshTokenValidTime = (60 * 1000) * 600;

    private static final String SECRET_KEY =
            "solsolsolsolsolsolsolsosloslsolsosloslosloslsolsolsososloslsolsoslssoolsolsolsolsosolsosloslosloslsoloslsolsolsoslosl"; // 비밀 키 (랜덤하고 보안 강화를 위해 변경하세요)


    public Token generateToken(String username, Authentication authentication) {

        log.info("GenerateToken....");
        // 인증된 사용자의 권한 목록 조회

        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        Date now = new Date();
        Date expiration = new Date(System.currentTimeMillis() + accessTokenValidTime); // 만료 시간
        log.info(expiration.toString());
        Date r_expiration = new Date(System.currentTimeMillis() + refreshTokenValidTime); // 만료 시간
        log.info(r_expiration.toString());

        String accessToken = Jwts.builder()
                .setSubject(authentication.getName())
                //Todo claim 설정
                .claim("auth", "ADMIN")
                .setExpiration(expiration)
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                .compact();
        // Refresh Token 생성
        String refreshToken = Jwts.builder()
                .setSubject(authentication.getName())
                .claim("auth", "ADMIN")
                .setExpiration(r_expiration)
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                .compact();
        log.info("saveRefreshToken key:"+username);
        saveRefreshToken(username, refreshToken);

        return  Token.builder()
                .grantType("Bearer")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }


    public String generateAccessToken(Authentication authentication) {
        Date now = new Date();
        Date expiration = new Date(System.currentTimeMillis() + accessTokenValidTime); // 만료 시간

        return  Jwts.builder()
                .setSubject(authentication.getName())
                .claim("auth", authentication)
                .setExpiration(expiration)
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                .compact();
    }


    public TokenCode validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token);
            return TokenCode.ACCESS_TOKEN;
        } catch (ExpiredJwtException e) {
            return TokenCode.EXPIRED_TOKEN;
        } catch (Exception e) {
            return TokenCode.INVALID_TOKEN;
        }
    }

    public Authentication getAuthentication(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
        log.info(claims.toString());

        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get("auth").toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        User principal = new User(claims.getSubject(), "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }


    public String getRefreshToken(String username) {
        return (String) redisTemplate.opsForValue().get(username);
    }
    public String extractAllClaims(String token) throws ExpiredJwtException {
            String subject = Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(token)
                    .getBody().getSubject();
            return subject;
    }



    public void saveRefreshToken(String username, String refreshToken) {
        redisTemplate.opsForValue().set(username, refreshToken,refreshTokenValidTime,TimeUnit.MILLISECONDS);
    }


}