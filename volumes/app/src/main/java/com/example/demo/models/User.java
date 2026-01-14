package com.example.demo.models;

import java.util.List;
import javax.persistence.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    // --- 追加: パスワードを保存するカラム ---
    @Column(nullable = false)
    private String password;

    // 1 : 多（ユーザー → お気に入り）
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Favorite> favorites;

    // コンストラクタ
    public User() {}
    
 // 管理者かユーザーかわける項目
    private String role; // "ROLE_ADMIN" または "ROLE_USER"

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    // Getter / Setter
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // --- 追加: PasswordのGetter/Setter ---
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Favorite> getFavorites() {
        return favorites;
    }

    public void setFavorites(List<Favorite> favorites) {
        this.favorites = favorites;
    }
    
    
}