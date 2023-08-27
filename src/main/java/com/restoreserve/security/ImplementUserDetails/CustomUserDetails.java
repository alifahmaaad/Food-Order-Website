package com.restoreserve.security.ImplementUserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.restoreserve.model.entities.User;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CustomUserDetails implements UserDetails {
    private Long id;
    private String username;
    private String password;
    private String role;
    private Set<GrantedAuthority> authorities = new HashSet<>();
    
   public CustomUserDetails(User user , String encodedPassword) {
    // System.out.print(user);
        this.id = user.getId();
        this.username = user.getUsername();
        this.password = user.getPassword();
        String role = "ROLE_" + user.getRole().toString();
        this.authorities.add(new SimpleGrantedAuthority(role));
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
}