package com.example.demo.config;

import com.example.demo.models.User;
import com.example.demo.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataLoader {

	@Bean
	public CommandLineRunner initData(UserRepository userRepository) {
	    return args -> {
	        // --- 管理者登録（強制更新） ---
	        User admin = userRepository.findByName("admin").orElse(new User());
	        admin.setName("admin");
	        admin.setPassword("ad123");
	        admin.setRole("ROLE_ADMIN");
	        userRepository.save(admin);
	        System.out.println("--- [CHECK] admin: ad123 / ROLE_ADMIN を保存しました ---");

	        // --- 一般ユーザー登録（強制更新に変更！） ---
	        User user = userRepository.findByName("user").orElse(new User());
	        user.setName("user");
	        user.setPassword("user123"); // パスワードはこれ！
	        user.setRole("ROLE_USER");
	        userRepository.save(user);
	        System.out.println("--- [CHECK] user: user123 / ROLE_USER を保存しました ---");
	    };
	}
}