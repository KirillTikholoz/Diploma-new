package auth.common.domain;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Collection;

@Entity
@Table(name = "OAuthUsr")
@Data
public class OAuthUser {
    @Id
    private Long id;
    private String firstName;
    private String lastName;

    @ManyToMany
    @JoinTable(
            name = "oauth_users_roles",
            joinColumns = @JoinColumn(name = "oauth_user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Collection<Role> roles;

}
