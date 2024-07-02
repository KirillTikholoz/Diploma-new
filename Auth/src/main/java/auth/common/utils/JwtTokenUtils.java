package auth.common.utils;

import auth.common.UserDetails.CustomUserDetails;
import auth.common.UserDetails.OAuthUserDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

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

    public String generateRefreshToken(UserDetails customUserDetails){
        Date issuedDate = new Date();
        Date expiredDate = new Date(issuedDate.getTime() + lifetimeRefresh.toMillis());
        return Jwts.builder()
                //.setClaims(claims)
                .setSubject(customUserDetails.getUsername())
                .setIssuedAt(issuedDate)
                .setExpiration(expiredDate)
                .signWith(Keys.hmacShaKeyFor(secretRefresh.getBytes()), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateAccessTokenForUserVk(OAuthUserDetails oAuthUserDetails){
        Map<String, Object> claims = new HashMap<>();
        List<String> RoleList = oAuthUserDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        claims.put("roles", RoleList);

        String firstName = oAuthUserDetails.getFirstName();
        String lastName = oAuthUserDetails.getLastName();
        claims.put("firstName", firstName);
        claims.put("lastName", lastName);

        Date issuedDate = new Date();
        Date expiredDate = new Date(issuedDate.getTime() + jwtLifetime.toMillis());
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(oAuthUserDetails.getUsername())
                .setIssuedAt(issuedDate)
                .setExpiration(expiredDate)
                .signWith(Keys.hmacShaKeyFor(secretAccess.getBytes()), SignatureAlgorithm.HS256)
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
    public List<String> getRoles(String token){
        return getAllClaimsFromAccessToken(token).get("roles", List.class);
    }

}
