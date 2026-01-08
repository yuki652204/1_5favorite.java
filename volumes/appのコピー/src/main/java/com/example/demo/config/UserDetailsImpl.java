package com.example.demo.config;

import java.util.Collection;
import java.util.Collections;
import com.example.demo.models.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class UserDetailsImpl implements UserDetails {
    private final User user;

    public UserDetailsImpl(User user) {
        this.user = user;
    }

    
    
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList(); // 今回は権限（ロール）は空でOK
    }
    
    @Override
    public String getPassword() {
        // 空ではなく、何かしら適当な文字を返しておきます
        return "password";
    }


    @Override
    public String getUsername() {
        return user.getName(); // DBのnameをログインIDとして使う
    }

    // 全て true に設定してください
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}