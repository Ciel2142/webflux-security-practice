package org.practice.wtf.justpracticeproject.security;

import org.practice.wtf.justpracticeproject.domain.CustomUser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;


public class CustomUserDetail implements UserDetails {

    private final String userName;
    private final String password;
    private final List<SimpleGrantedAuthority> roles;

    public CustomUserDetail(CustomUser user) {
        this.roles = user.getAuthorities();
        this.password = user.getPassword();
        this.userName = user.getUserName();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.userName;
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
}
