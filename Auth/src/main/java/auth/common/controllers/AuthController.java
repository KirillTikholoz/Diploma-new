package auth.common.controllers;

import auth.common.UserDetails.CustomUserDetails;
import auth.common.domain.User;
import auth.common.services.UserService;
import auth.common.dtos.JwtRequest;
import auth.common.exception.AppError;
import auth.common.utils.JwtTokenUtils;
import jakarta.annotation.security.PermitAll;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private final UserService userService;
    private final JwtTokenUtils jwtTokenUtils;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/auth")
    @PermitAll
    public ResponseEntity<?> createAuthToken(@RequestBody JwtRequest authRequest){
        try {
            Optional<User> user = userService.findByUsername(authRequest.getUsername());
            if (user.isEmpty()) {
                return ResponseEntity.badRequest().body("Пользователя с таким логином не существует");
            }
            String saltPassword = user.get().getSalt() + authRequest.getPassword();
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUsername(), saltPassword));

        } catch (BadCredentialsException e){
            return new ResponseEntity<>(new AppError(HttpStatus.UNAUTHORIZED.value(), "Неправильный логин или пароль"), HttpStatus.UNAUTHORIZED);
        }

        CustomUserDetails customUserDetails = userService.loadUserByUsername(authRequest.getUsername());

        String accessToken = jwtTokenUtils.generateAccessToken(customUserDetails);
        String refreshToken = jwtTokenUtils.generateRefreshToken(customUserDetails);

        Cookie accessTokenCookie = new Cookie("accessToken", accessToken);
        Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);

        accessTokenCookie.setMaxAge(86400); // 24 часа
        refreshTokenCookie.setMaxAge(86400);

        accessTokenCookie.setHttpOnly(true); // флаг HttpOnly
        refreshTokenCookie.setHttpOnly(true);

        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getResponse();

        response.addCookie(accessTokenCookie);
        response.addCookie(refreshTokenCookie);

        return ResponseEntity.ok("Токены созданы");
    }

}
