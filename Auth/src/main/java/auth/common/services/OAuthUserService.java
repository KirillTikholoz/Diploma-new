package auth.common.services;

import auth.common.UserDetails.OAuthUserDetails;
import auth.common.domain.OAuthUser;
import auth.common.repo.OAuthUserRepository;
import auth.common.repo.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OAuthUserService implements UserDetailsService {
    private final OAuthUserRepository oAuthUserRepository;
    private final RoleRepository roleRepository;

    public Optional<OAuthUser> findById(Long id){
        return oAuthUserRepository.findById(id);
    }

    @Override
    @Transactional
    public OAuthUserDetails loadUserByUsername(String id) throws UsernameNotFoundException {
        throw new UsernameNotFoundException("Method not supported");
    }

    @Transactional
    public OAuthUserDetails loadUserById(Long id) throws UsernameNotFoundException {
        OAuthUser oAuthUser = findById(id).orElseThrow(() -> new UsernameNotFoundException(
                String.format("Пользователь с id='%s' не найден", id)
        ));

        return OAuthUserDetails.create(oAuthUser);
    }

    public OAuthUser createNewUser(OAuthUser oAuthUser){
        // добавить проверку на существует ли такая роль + остальные проверки
        oAuthUser.setRoles(List.of(roleRepository.findByName("ROLE_USER").get()));
        return oAuthUserRepository.save(oAuthUser);
    }
}
