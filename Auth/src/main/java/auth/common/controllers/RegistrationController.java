package auth.common.controllers;

import auth.common.dtos.RegistrateUserDto;
import auth.common.services.UserService;
import jakarta.annotation.security.PermitAll;
import auth.common.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/registrate")
public class RegistrationController {
    private static final Logger logger = LoggerFactory.getLogger(RegistrationController.class);
    @Autowired
    private UserService userService;

    @PostMapping
    @PermitAll
    public ResponseEntity<String> registrateUser(@RequestBody RegistrateUserDto registrateUserDto){
        Optional<User> existingUser = userService.findByUsername(registrateUserDto.getUsername());
        if (existingUser.isPresent()){
            return ResponseEntity.badRequest().body("Пользователь с таким логином уже существует");
        }

        if (registrateUserDto.getFirstName() == null || registrateUserDto.getLastName() == null) {
            return ResponseEntity.badRequest().body("Имя и фамилия обязательны для регистрации");
        }

        userService.createNewUser(registrateUserDto);

        return ResponseEntity.created(null).body("Пользователь успешно зарегистрирован");
    }

}
