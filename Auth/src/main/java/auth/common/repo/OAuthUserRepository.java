package auth.common.repo;

import auth.common.domain.OAuthUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OAuthUserRepository extends JpaRepository<OAuthUser, Long> {
    Optional<OAuthUser> findByUsername(String username);
    OAuthUser findByFirstName(String firstName);
    OAuthUser findByLastName(String lastName);

}
