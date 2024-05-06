package auth.common.UserDetails;

import auth.common.domain.OAuthUser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.stream.Collectors;

public class OAuthUserDetails implements UserDetails {
    private final String first_name;
    private final String last_name;
    private final Long id;
    private final Collection<? extends GrantedAuthority> authorities;


    public OAuthUserDetails(Long id, String first_name, String last_name, Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.first_name = first_name;
        this.last_name = last_name;
        this.authorities = authorities;
    }

    public static OAuthUserDetails create(OAuthUser user) {
        Collection<SimpleGrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());

        System.out.println("Roles = " + user.getRoles());

        return new OAuthUserDetails(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getRoles().stream().map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList())
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return first_name;
    }

    public String getLastName() {
        return last_name;
    }

    public Long getId() {
        return id;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Учетная запись никогда не истекает
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Учетная запись не заблокирована
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Срок действия учетных данных никогда не истекает
    }

    @Override
    public boolean isEnabled() {
        return true; // Учетная запись всегда включена
    }
}
