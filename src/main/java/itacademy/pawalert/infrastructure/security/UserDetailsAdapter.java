package itacademy.pawalert.infrastructure.security;

import itacademy.pawalert.domain.user.Role;
import itacademy.pawalert.domain.user.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class UserDetailsAdapter implements UserDetails {

    @Getter
    private final User user;
    private final String password;
    private final List<GrantedAuthority> authorities;

    public UserDetailsAdapter(User user, String password) {
        this.user = user;
        this.password = password;
        this.authorities = List.of(new SimpleGrantedAuthority("ROLE_" + user.role().name()));
    }

    @Override
    public String getUsername() {
        return user.username().value();
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public <Optional> Role getUserRole() {
        return user.role();
    }

}
