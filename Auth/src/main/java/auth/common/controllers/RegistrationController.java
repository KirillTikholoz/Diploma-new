package auth.common.controllers;

import auth.common.services.UserService;
import jakarta.annotation.security.PermitAll;
import auth.common.domain.User;
import auth.common.repo.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Optional;

@RestController
@RequestMapping("/registrate")
public class RegistrationController {
    private static final Logger logger = LoggerFactory.getLogger(RegistrationController.class);
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private UserService userService;

    @PostMapping
    @PermitAll
    public ResponseEntity<String> registrateUser(@RequestBody User user){
        Optional<User> existingUser = userService.findByUsername(user.getUsername());
        if (existingUser.isPresent()){
            return ResponseEntity.badRequest().body("Пользователь с таким логином уже существует");
        }

        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        String saltString = Base64.getEncoder().encodeToString(salt);
        user.setSalt(saltString);

        String hashedPassword = passwordEncoder.encode(saltString + user.getPassword());
        user.setPassword(hashedPassword);

//        user.setFirstName("Пустое имя");
//        user.setLastName("Пустая фамилия");

        userService.createNewUser(user);

        return ResponseEntity.created(null).body("Пользователь успешно зарегистрирован");
    }

}
