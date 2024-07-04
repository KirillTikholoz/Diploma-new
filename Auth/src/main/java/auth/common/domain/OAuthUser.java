package auth.common.domain;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Collection;

@Entity
@Table(name = "oauth_usr")
@Data
public class OAuthUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
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
