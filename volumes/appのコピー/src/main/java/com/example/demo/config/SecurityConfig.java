package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter { // ← 継承する形に変えます

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
                .antMatchers("/css/**", "/js/**").permitAll()
                .anyRequest().authenticated()
            .and()
            .formLogin()
                .defaultSuccessUrl("/user", true)
            .and()
            .logout()
                .logoutSuccessUrl("/login")
            .and()
            .csrf().disable();
    }

 // SecurityConfig.java 内の重要な Bean 設定
    @Bean
    public PasswordEncoder passwordEncoder() {
        // DBが平文（password123など）なので、一旦これを使います
        return org.springframework.security.crypto.password.NoOpPasswordEncoder.getInstance();
    }
}