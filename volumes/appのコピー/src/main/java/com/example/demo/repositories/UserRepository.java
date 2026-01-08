package com.example.demo.repositories;

import java.util.Optional; // 追加
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.models.User;

public interface UserRepository extends JpaRepository<User, Long> {
    // 戻り値を Optional にすることで、見つからなかった場合の処理が書きやすくなります
    Optional<User> findByName(String name);
}