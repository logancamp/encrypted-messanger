package dev.camp.MessageApp.security;

import java.util.Collection;
import java.util.List;

import dev.camp.MessageApp.models.types.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class UserPrincipal implements UserDetails {
    private User user;

    public UserPrincipal(User user) {
        this.user = user;
    }

    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (!user.verified) {
            return List.of();
        }
        return List.of(new SimpleGrantedAuthority(user.role));
    }

    public String getPassword() {
        return this.user.password;
    }

    public String getUsername() {
        return this.user.username;
    }

    public Long getId() { return this.user.id; }
}
