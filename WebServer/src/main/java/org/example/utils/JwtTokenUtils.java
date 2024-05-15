package org.example.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class JwtTokenUtils {
    @Value("${jwt.secretAccess}")
    private String secretAccess;

    private Claims getAllClaimsFromAccessToken(String token){
        Keys.secretKeyFor(SignatureAlgorithm.HS256);
        return Jwts.parser()
                .setSigningKey(Keys.hmacShaKeyFor(secretAccess.getBytes()))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String getUsernameAccessToken(String token){
        return getAllClaimsFromAccessToken(token).getSubject();
    }
    public String getNameFromAccessToken(String token){
        String firstName = getAllClaimsFromAccessToken(token).get("firstName", String.class);
        String lastName = getAllClaimsFromAccessToken(token).get("lastName", String.class);
        if (firstName!=null && lastName!=null) {
            String name = firstName + " " + lastName;
            return name;
        } else {
            return "Неизвестный пользователь";
        }
    }
    public List<String> getRoles(String token){
        return getAllClaimsFromAccessToken(token).get("roles", List.class);
    }
    public Long getExpirationTimeAccessToken(String token){
        return getAllClaimsFromAccessToken(token).getExpiration().getTime();
    }
}
