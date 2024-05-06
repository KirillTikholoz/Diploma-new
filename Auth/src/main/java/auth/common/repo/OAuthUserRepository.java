package auth.common.repo;

import auth.common.domain.OAuthUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OAuthUserRepository extends JpaRepository<OAuthUser, Long> {
    OAuthUser findByFirstName(String firstName);
    OAuthUser findByLastName(String lastName);

}

