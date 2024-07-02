package auth.common.services;

import auth.common.UserDetails.OAuthUserDetails;
import auth.common.domain.OAuthUser;
import auth.common.dtos.VkResponseUserInfoDto;
import auth.common.repo.OAuthUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OAuthUserService implements UserDetailsService {
    private final OAuthUserRepository oAuthUserRepository;
    private final RoleService roleService;

    public Optional<OAuthUser> findById(Long id){
        return oAuthUserRepository.findById(id);
    }
    public Optional<OAuthUser> findByUsername(String username){
        return oAuthUserRepository.findByUsername(username);
    }

    @Override
    @Transactional
    public OAuthUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        OAuthUser oAuthUser = findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(
                String.format("Пользователь с id='%s' не найден", username)
        ));

        return OAuthUserDetails.create(oAuthUser);
    }
    @Transactional
    public OAuthUserDetails loadUserById(Long id) throws UsernameNotFoundException {
        OAuthUser oAuthUser = findById(id).orElseThrow(() -> new UsernameNotFoundException(
                String.format("Пользователь с id='%s' не найден", id)
        ));

        return OAuthUserDetails.create(oAuthUser);
    }

    public OAuthUser createNewUser(VkResponseUserInfoDto vkResponseUserInfoDto){
        OAuthUser newOAuthUser = new OAuthUser();

        newOAuthUser.setUsername(vkResponseUserInfoDto.getId());
        newOAuthUser.setFirstName(vkResponseUserInfoDto.getFirst_name());
        newOAuthUser.setLastName(vkResponseUserInfoDto.getLast_name());
        newOAuthUser.setRoles(List.of(roleService.getUserRole()));

        return oAuthUserRepository.save(newOAuthUser);
    }
}
