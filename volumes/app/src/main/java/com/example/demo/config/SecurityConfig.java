package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.beans.factory.annotation.Autowired;
@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Autowired
	private CustomLoginSuccessHandler successHandler; // ステップ1で作ったクラスを注入

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
	    http
	        .authorizeRequests(authorize -> authorize
	            .antMatchers("/login", "/css/**").permitAll() // ログイン画面は全員OK
	            .antMatchers("/admin/**").hasRole("ADMIN")    // 管理画面はADMINのみ
	            .anyRequest().authenticated()
	        )
	        .formLogin(login -> login
	            .loginPage("/login")             // 入り口は一つ
	            .loginProcessingUrl("/login")    // 送信先も一つ
	            .successHandler(successHandler)  // ★ここで「振り分け役」を呼び出す
	            .permitAll()
	        )
	        .logout(logout -> logout
	            .logoutSuccessUrl("/login?logout")
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