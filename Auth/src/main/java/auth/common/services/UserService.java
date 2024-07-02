package auth.common.services;

import auth.common.UserDetails.CustomUserDetails;
import auth.common.dtos.RegistrateUserDto;
import auth.common.repo.UserRepository;
import auth.common.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final RoleService roleService;
    private final BCryptPasswordEncoder passwordEncoder;

    public Optional<User> findByUsername(String username){
        return Optional.ofNullable(userRepository.findByUsername(username));
    }

    @Override
    @Transactional
    public CustomUserDetails loadUserByUsername(String username) throws UsernameNotFoundException{
        User user = findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(
                String.format("Пользователь '%s' не найден", username)
        ));

        return CustomUserDetails.create(user);
    }

    public void createNewUser(RegistrateUserDto registrateUserDto){
        User newUser = new User();

        newUser.setUsername(registrateUserDto.getUsername());
        newUser.setFirstName(registrateUserDto.getFirstName());
        newUser.setLastName(registrateUserDto.getLastName());

        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        String saltString = Base64.getEncoder().encodeToString(salt);
        newUser.setSalt(saltString);

        String hashedPassword = passwordEncoder.encode(saltString + registrateUserDto.getPassword());
        newUser.setPassword(hashedPassword);

        newUser.setRoles(List.of(roleService.getUserRole()));
        userRepository.save(newUser);
    }

}
