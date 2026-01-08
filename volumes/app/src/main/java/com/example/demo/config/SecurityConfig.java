package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
	    http
	        .authorizeRequests(authorize -> authorize
	            // CSSなどは引き続き許可
	            .antMatchers("/static/**", "/css/**", "/style.css").permitAll()
	            .anyRequest().authenticated()
	        )
	        .formLogin(login -> login
	            // .loginPage("/login") をコメントアウト（または削除）
	            .defaultSuccessUrl("/", true)
	            .permitAll()
	        )
	        .logout(logout -> logout
	            .permitAll()
	        );
	    
	    return http.build();
	}

    @Bean
    public PasswordEncoder passwordEncoder() {
        // 元々ログインできていた時、パスワードを暗号化していなければこれが必要です
        return NoOpPasswordEncoder.getInstance();
    }
}