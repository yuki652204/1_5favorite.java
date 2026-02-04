package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // --- 管理者用の設定 ---
    @Bean
    @Order(1) // 1番目にチェックするルール
    public SecurityFilterChain adminSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .antMatcher("/admin/**") // このチェーンは /admin/ で始まるURLの時だけ動く
            .authorizeRequests(authorize -> authorize
                .antMatchers("/admin/login", "/css/**").permitAll()
                .anyRequest().hasRole("ADMIN")
            )
            .formLogin(login -> login
                .loginPage("/admin/login")         // 管理者専用ログイン画面
                .loginProcessingUrl("/admin/login") // 管理者専用の認証処理URL
                .defaultSuccessUrl("/admin/list", true) // 成功時の遷移先
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/admin/logout")
                .logoutSuccessUrl("/admin/login?logout")
                .permitAll()
            );

        return http.build();
    }

    // --- 一般ユーザー用の設定 ---
    @Bean
    @Order(2) // 2番目にチェックするルール（その他すべて）
    public SecurityFilterChain userSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeRequests(authorize -> authorize
                .antMatchers("/user/login", "/css/**").permitAll()
                .anyRequest().authenticated()
            )
            .formLogin(login -> login
                .loginPage("/user/login")          // ユーザー専用ログイン画面
                .loginProcessingUrl("/user/login")  // ユーザー専用の認証処理URL
                .defaultSuccessUrl("/user/home", true)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/user/logout")
                .logoutSuccessUrl("/user/login?logout")
                .permitAll()
            );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }
}