package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
// --- ↓ この import を追加 ---
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // --- 管理者用の設定 ---
    @Bean
    @Order(1)
    public SecurityFilterChain adminSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .antMatcher("/admin/**")
            .authorizeRequests(authorize -> authorize
                .antMatchers("/admin/login", "/css/**").permitAll()
                .anyRequest().hasRole("ADMIN")
            )
            .formLogin(login -> login
                .loginPage("/admin/login")
                .loginProcessingUrl("/admin/login")
                .defaultSuccessUrl("/admin/list", true)
                .permitAll()
            )
            .logout(logout -> logout
                // --- ↓ ここを修正：GETでのログアウトを許可する ---
                .logoutRequestMatcher(new AntPathRequestMatcher("/admin/logout"))
//   通常、Spring Securityは /logout というURLに対して「POST」で来た時だけ反応します。
//                .logoutRequestMatcher(new AntPathRequestMatcher("/admin/logout")) と書くことで、
//                「URLがこれなら、メソッド（GET/POST）を問わずにログアウト処理として受け付けるよ！」 
//                という設定に上書きされるため、リンククリック（GET）でも404にならなくなります。             
                
                .logoutSuccessUrl("/admin/login?logout")
                .deleteCookies("JSESSIONID") // ついでにクッキーも消すとより安全
                .invalidateHttpSession(true) // セッションを確実に無効化
                .permitAll()
            );

        return http.build();
    }

    // --- 一般ユーザー用の設定 ---
    @Bean
    @Order(2)
    public SecurityFilterChain userSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeRequests(authorize -> authorize
                .antMatchers("/user/login", "/css/**").permitAll()
                .anyRequest().authenticated()
            )
            .formLogin(login -> login
                .loginPage("/user/login")
                .loginProcessingUrl("/user/login")
                .defaultSuccessUrl("/user/home", true)
                .permitAll()
            )
            .logout(logout -> logout
                // --- ↓ ここを修正：GETでのログアウトを許可する ---
                .logoutRequestMatcher(new AntPathRequestMatcher("/user/logout"))
                .logoutSuccessUrl("/user/login?logout")
                .deleteCookies("JSESSIONID")
                .invalidateHttpSession(true)
                .permitAll()
            );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }
}