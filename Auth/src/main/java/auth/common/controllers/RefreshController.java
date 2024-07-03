package auth.common.controllers;

import auth.common.UserDetails.CustomUserDetails;
import auth.common.services.OAuthUserService;
import auth.common.services.UserService;
import auth.common.utils.JwtTokenUtils;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.security.PermitAll;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping()
public class RefreshController {
    private final UserService userService;
    private final OAuthUserService oAuthUserService;
    private final JwtTokenUtils jwtTokenUtils;

    @PostMapping("/refresh")
    @PermitAll
    public ResponseEntity<?> refreshTokens(@CookieValue(value = "refreshToken", required = false) String refreshTokenArg){
        try {
            String username = jwtTokenUtils.getUsernameRefreshToken(refreshTokenArg);
            String type = jwtTokenUtils.getTypeRefreshToken(refreshTokenArg);

            try {
                if (type.equals("Simple")) {
                    CustomUserDetails customUserDetails = userService.loadUserByUsername(username);
                    String accessToken = jwtTokenUtils.generateAccessToken(customUserDetails);
                    String refreshToken = jwtTokenUtils.generateRefreshTokenSimpleUser(customUserDetails);
                    jwtTokenUtils.makeCookies(accessToken, refreshToken);

                } else {
                    CustomUserDetails customUserDetails = oAuthUserService.loadUserByUsername(username);
                    String accessToken = jwtTokenUtils.generateAccessToken(customUserDetails);
                    String refreshToken = jwtTokenUtils.generateRefreshTokenUserVk(customUserDetails);
                    jwtTokenUtils.makeCookies(accessToken, refreshToken);
                }

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
