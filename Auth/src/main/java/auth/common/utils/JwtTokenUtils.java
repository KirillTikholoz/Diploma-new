package auth.common.utils;

import auth.common.UserDetails.CustomUserDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Component
public class JwtTokenUtils {
    @Value("${jwt.secretAccess}")
    private String secretAccess;
    @Value("${jwt.secretRefresh}")
    private String secretRefresh;
    @Value("${jwt.lifetime}")
    private Duration jwtLifetime;
    @Value("${jwt.lifetimeRefresh}")
    private Duration lifetimeRefresh;

    public String generateAccessToken(CustomUserDetails customUserDetails){
        Map<String, Object> claims = new HashMap<>();
        List<String> RoleList = customUserDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        claims.put("roles", RoleList);

        String firstName = customUserDetails.getFirstName();
        String lastName = customUserDetails.getLastName();
        claims.put("firstName", firstName);
        claims.put("lastName", lastName);

        Date issuedDate = new Date();
        Date expiredDate = new Date(issuedDate.getTime() + jwtLifetime.toMillis());
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(customUserDetails.getUsername())
                .setIssuedAt(issuedDate)
                .setExpiration(expiredDate)
                .signWith(Keys.hmacShaKeyFor(secretAccess.getBytes()), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshTokenSimpleUser(UserDetails userDetails){
        Map<String, Object> claims = new HashMap<>();

        Date issuedDate = new Date();
        Date expiredDate = new Date(issuedDate.getTime() + lifetimeRefresh.toMillis());
        claims.put("type", "Simple");

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(issuedDate)
                .setExpiration(expiredDate)
                .signWith(Keys.hmacShaKeyFor(secretRefresh.getBytes()), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshTokenUserVk(UserDetails userDetails){
        Map<String, Object> claims = new HashMap<>();
        Date issuedDate = new Date();
        Date expiredDate = new Date(issuedDate.getTime() + lifetimeRefresh.toMillis());

        claims.put("type", "Vk");
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(issuedDate)
                .setExpiration(expiredDate)
                .signWith(Keys.hmacShaKeyFor(secretRefresh.getBytes()), SignatureAlgorithm.HS256)
                .compact();
    }

    private Claims getAllClaimsFromAccessToken(String token){
        Keys.secretKeyFor(SignatureAlgorithm.HS256);
        return Jwts.parser()
                .setSigningKey(Keys.hmacShaKeyFor(secretAccess.getBytes()))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Claims getAllClaimsFromRefreshToken(String token){
        Keys.secretKeyFor(SignatureAlgorithm.HS256);
        return Jwts.parser()
                .setSigningKey(Keys.hmacShaKeyFor(secretRefresh.getBytes()))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String getUsernameAccessToken(String token){
        return getAllClaimsFromAccessToken(token).getSubject();
    }
    public String getUsernameRefreshToken(String token){
        return getAllClaimsFromRefreshToken(token).getSubject();
    }
    public String getTypeRefreshToken(String token){
        return getAllClaimsFromRefreshToken(token).get("type", String.class);
    }
    public List<String> getRoles(String token){
        return getAllClaimsFromAccessToken(token).get("roles", List.class);
    }
    public void makeCookies(String accessToken, String refreshToken){
        Cookie accessTokenCookie = new Cookie("accessToken", accessToken);
        Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);

        accessTokenCookie.setMaxAge(86400); // 24 часа
        refreshTokenCookie.setMaxAge(86400);

        accessTokenCookie.setHttpOnly(true); // флаг HttpOnly
        refreshTokenCookie.setHttpOnly(true);

        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getResponse();

        response.addCookie(accessTokenCookie);
        response.addCookie(refreshTokenCookie);
    }

}
