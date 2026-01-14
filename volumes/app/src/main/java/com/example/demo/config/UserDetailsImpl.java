package com.example.demo.config;

import java.util.Collection;
import com.example.demo.models.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;

public class UserDetailsImpl implements UserDetails {
    private final User user;

    public UserDetailsImpl(User user) {
        this.user = user;
    }

    // 重複を解消：roleを返す方だけを残します
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // データベースのrole文字列（ROLE_ADMINなど）を権限として返します
        return AuthorityUtils.createAuthorityList(user.getRole());
    }
    
    @Override
    public String getPassword() {
        // DBから取得したパスワードを返します
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getName(); // DBのnameをログインIDとして使う
    }

    // 全て true に設定
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}