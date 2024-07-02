package auth.common.controllers;

import auth.common.UserDetails.CustomUserDetails;
import auth.common.services.UserService;
import auth.common.utils.JwtTokenUtils;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.security.PermitAll;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@RestController
@RequiredArgsConstructor
@RequestMapping()
public class RefreshController {
    private final UserService userService;
    private final JwtTokenUtils jwtTokenUtils;

    @PostMapping("/refresh")
    @PermitAll
    public ResponseEntity<?> refreshTokens(@CookieValue(value = "refreshToken", required = false) String refreshTokenArg){
        try {
            String username = jwtTokenUtils.getUsernameRefreshToken(refreshTokenArg);

            try {
                CustomUserDetails customUserDetails = userService.loadUserByUsername(username);

                String accessToken = jwtTokenUtils.generateAccessToken(customUserDetails);
                String refreshToken = jwtTokenUtils.generateRefreshToken(customUserDetails);

                Cookie accessTokenCookie = new Cookie("accessToken", accessToken);
                Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);

                accessTokenCookie.setMaxAge(86400);
                refreshTokenCookie.setMaxAge(86400);

                accessTokenCookie.setHttpOnly(true); // флаг HttpOnly
                refreshTokenCookie.setHttpOnly(true);

                HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getResponse();

                response.addCookie(accessTokenCookie);
                response.addCookie(refreshTokenCookie);

                return ResponseEntity.ok("Refresh token действителен");

            } catch (UsernameNotFoundException e) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Пользователя с таким именем не существует");
            }


        } catch (ExpiredJwtException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Refresh token истек");
        } catch (SignatureException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Ошибка валидации подписи токена");
        }
    }
}
