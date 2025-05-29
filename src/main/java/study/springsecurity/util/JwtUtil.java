package study.springsecurity.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class JwtUtil {
    @Value("${jwt.secret}")
    private String secret;

    private static final long ACCESS_TOKEN_TIMEOUT = (long) 1000 * 60 * 30; // 액세스 토큰 만료 시간 30분
//    private static final long REFRESH_TOKEN_TIMEOUT = (long) 1000 * 60 * 60 * 24 * 14; // 리프레시 토큰 만료 시간 14일

    private SecretKey key;

    @PostConstruct
    public void init() {
        key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    /**
     * 토큰에서 사용자 이름(subject) 추출
     */
    public String getUsernameFromToken(final String token) {
        // 토큰 유효성 검증
        if(Boolean.FALSE.equals(validateToken(token))) {
            return null;
        }

        Claims claims = getAllClaimsFromToken(token);
        return claims.getSubject();
    }

    /**
     * 토큰에서 만료 일자 추출
     */
    public Date getExpirationDateFromToken(final String token) {
        // 토큰 유효성 검증
        if(Boolean.FALSE.equals(validateToken(token))) {
            return null;
        }

        Claims claims = getAllClaimsFromToken(token);
        return claims.getExpiration();
    }

    /**
     * 토큰에서 모든 클레임 정보 추출
     */
    private Claims getAllClaimsFromToken(final String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 사용자 ID로 액세스 토큰 생성
     */
    public String generateAccessToken(final String username) {
        Map<String, Object> claims = new HashMap<>();
        return generateAccessToken(username, claims);
    }

    /**
     * 숫자형 사용자 ID로 액세스 토큰 생성
     */
    public String generateAccessToken(final long username) {
        return generateAccessToken(String.valueOf(username));
    }

    /**
     * 사용자 정보와 추가 클레임으로 액세스 토큰 생성
     */
    public String generateAccessToken(final String username, final Map<String, Object> claims) {
        // 현재 시간과 만료 시간 계산
        Date now = new Date(System.currentTimeMillis());
        Date expiryDate = new Date(now.getTime() + ACCESS_TOKEN_TIMEOUT);

        // JWT 토큰 생성
        return Jwts.builder()
                .setClaims(claims)           // 추가 정보
                .setSubject(username)         // 사용자 식별자
                .setIssuedAt(now)            // 발급 시간
                .setExpiration(expiryDate)   // 만료 시간
                .signWith(key)               // 서명
                .compact();                  // 토큰 생성
    }

    /**
     * token 검증
     *
     * @param token JWT
     * @return token 검증 결과
     */
    public Boolean validateToken(final String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (SecurityException e) {
            log.warn("Invalid JWT signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.warn("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.warn("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.warn("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.warn("JWT claims string is empty: {}", e.getMessage());
        }

        return false;
    }

//    /**
//     * refresh token 생성
//     *
//     * @param subject token 제목
//     * @return refresh token
//     */
//    public String generateRefreshToken(final String subject) {
//        return doGenerateRefreshToken(subject);
//    }
//
//    /**
//     * refresh token 생성
//     *
//     * @param subject token 제목
//     * @return refresh token
//     */
//    public String generateRefreshToken(final long subject) {
//        return doGenerateRefreshToken(String.valueOf(subject));
//    }
//
//    /**
//     * refresh token 생성
//     *
//     * @param subject token 제목
//     * @return refresh token
//     */
//    private String doGenerateRefreshToken(final String subject) {
//        return Jwts.builder()
//                .setSubject(subject)
//                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_TIMEOUT))
//                .setIssuedAt(new Date(System.currentTimeMillis()))
//                .signWith(key)
//                .compact();
//    }
}
