package com.example.demo.models;

import java.time.LocalDateTime;
import javax.persistence.*;

@Entity
@Table(
    name = "favorites",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "product_id"})
    }
)
public class Favorite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 多 : 1（お気に入り → ユーザー）
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 多 : 1（お気に入り → 商品）
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    // Getter / Setter
    public Long getId() { return id; }
    public User getUser() { return user; }
    public Product getProduct() { return product; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setId(Long id) { this.id = id; }
    public void setUser(User user) { this.user = user; }
    public void setProduct(Product product) { this.product = product; }
}
