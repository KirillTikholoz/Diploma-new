package auth.common.services;

import auth.common.domain.Role;
import auth.common.repo.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;

    private Role getRole(String role){
        Optional<Role> existRole = roleRepository.findByName(role);
        if (existRole.isPresent()){
            return existRole.get();
        }
        else {
            Role newRole = new Role();
            newRole.setName(role);

            roleRepository.save(newRole);
            return newRole;
        }
    }

    public Role getUserRole(){
        return getRole("ROLE_USER");
    }

    public Role getAdminRole(){
        return getRole("ROLE_ADMIN");
    }

    public Role getModeratorRole(){
        return getRole("ROLE_MODERATOR");
    }
}
