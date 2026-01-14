package com.example.demo.models;

// 日付と時刻を扱うためのクラス
import java.time.LocalDateTime;

// JPA（Java Persistence API）のアノテーションをインポート
import javax.persistence.*;

// 【重要】Productクラスは entity パッケージ配下にあるため、正しいパスでインポート
import com.example.demo.models.entity.Product;

/**
 * お気に入り機能のエンティティクラス
 * ユーザーと商品の「多対多」の関係を管理する中間テーブルに対応
 * データベースの「favorites」テーブルと紐づく
 */
@Entity // このクラスがデータベースのテーブルと対応することを宣言
@Table(
    name = "favorites", // データベースのテーブル名を指定
    uniqueConstraints = {
        // 同じユーザーが同じ商品を複数回お気に入りできないように一意制約を設定
        @UniqueConstraint(columnNames = {"user_id", "product_id"})
    }
)
public class Favorite {
    
    // 【主キー（Primary Key）】
    @Id // この項目が主キーであることを宣言
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 自動採番（AUTO_INCREMENT）
    private Long id;

    // 【リレーション：多対一（お気に入り → ユーザー）】
    // 多くのお気に入りが1人のユーザーに紐づく
    @ManyToOne(fetch = FetchType.LAZY) // 遅延読み込み：必要になるまでUserデータを取得しない
    @JoinColumn(name = "user_id", nullable = false) // 外部キー列名を指定、NULL不可
    private User user;

    // 【リレーション：多対一（お気に入り → 商品）】
    // 多くのお気に入りが1つの商品に紐づく
    @ManyToOne(fetch = FetchType.LAZY) // 遅延読み込み：必要になるまでProductデータを取得しない
    @JoinColumn(name = "product_id", nullable = false) // 外部キー列名を指定、NULL不可
    private Product product;

    // 【お気に入り登録日時】
    @Column(name = "created_at", nullable = false) // データベースの列名と対応、NULL不可
    private LocalDateTime createdAt;

    /**
     * データベースへの保存前に自動的に実行されるメソッド
     * 登録日時を自動的に設定する
     */
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now(); // 現在日時を自動設定
    }

    // --- Getter（データを取得するメソッド）---
    
    /**
     * お気に入りIDを取得
     * @return お気に入りID
     */
    public Long getId() { 
        return id; 
    }
    
    /**
     * ユーザーを取得
     * @return お気に入りしたユーザー
     */
    public User getUser() { 
        return user; 
    }
    
    /**
     * 商品を取得
     * @return お気に入りされた商品
     */
    public Product getProduct() { 
        return product; 
    }
    
    /**
     * 登録日時を取得
     * @return お気に入りに登録した日時
     */
    public LocalDateTime getCreatedAt() { 
        return createdAt; 
    }

    // --- Setter（データを設定するメソッド）---
    
    /**
     * お気に入りIDを設定
     * @param id お気に入りID
     */
    public void setId(Long id) { 
        this.id = id; 
    }
    
    /**
     * ユーザーを設定
     * @param user お気に入りするユーザー
     */
    public void setUser(User user) { 
        this.user = user; 
    }
    
    /**
     * 商品を設定
     * @param product お気に入りする商品
     */
    public void setProduct(Product product) { 
        this.product = product; 
    }
}