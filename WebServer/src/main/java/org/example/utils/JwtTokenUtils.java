package org.example.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;

public class JwtTokenUtils {
    @Value("${jwt.secretAccess}")
    private String secretAccess;

    private Claims getAllClaimsFromAccessToken(String token){
        return Jwts.parser()
                .setSigningKey(secretAccess)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String getUsernameAccessToken(String token){
        return getAllClaimsFromAccessToken(token).getSubject();
    }
    public List<String> getRoles(String token){
        return getAllClaimsFromAccessToken(token).get("roles", List.class);
    }
    public Long getExpirationTimeAccessToken(String token){
        return getAllClaimsFromAccessToken(token).getExpiration().getTime();
    }
}
