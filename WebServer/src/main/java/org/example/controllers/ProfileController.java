package org.example.controllers;

import jakarta.annotation.security.PermitAll;
import lombok.RequiredArgsConstructor;
import org.example.dtos.NameResponseDto;
import org.example.utils.JwtTokenUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/profile")
public class ProfileController {
    private final JwtTokenUtils jwtTokenUtils;

    @GetMapping("/getName")
    public ResponseEntity<?> getName(@CookieValue(value = "accessToken", required = false) String accessTokenArg){
        try {
            String name = jwtTokenUtils.getNameFromAccessToken(accessTokenArg);
            return ResponseEntity.status(HttpStatus.OK).body(new NameResponseDto(name));
        } catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Проблемы с получением имени пользователя");
        }
    }
}
