package com.dreamsol.services.impl;

import com.dreamsol.entites.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Getter
@Setter
public class UserDetailsImpl implements UserDetails {
    private final User user;

    public UserDetailsImpl(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> grantedAuthorities;
        grantedAuthorities = new ArrayList<>();
        return grantedAuthorities;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return user.isStatus();
    }

    @Override
    public boolean isAccountNonLocked() {
        return user.isStatus();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return user.isStatus();
    }

    @Override
    public boolean isEnabled() {
        return user.isStatus();
    }
}
