 //リファクタリングすると
//アノテーションをつけると
//package com.example.demo.models;
//
//import lombok.Data;
//import lombok.NoArgsConstructor;
//import java.util.List;
//import java.util.ArrayList;
//import javax.persistence.*;
//import javax.validation.constraints.Min;
//
//@Entity
//@Table(name = "products")
//@Data             // Getter, Setter, toString などを全部自動作成！
//@NoArgsConstructor // 中身が空のコンストラクタを自動作成！
//public class Product {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @Column(nullable = false, length = 100)
//    private String name;
//
//    @Column(length = 500)
//    private String description;
//
//    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    private List<ProductVariant> variants = new ArrayList<>();
//
//    @Min(value = 0, message = "価格は0円以上に設定してください")
//    private int price;
//
//    // --- ここにあった大量の Getter / Setter は、@Data が全部やってくれるので削除！ ---
//
//    // ロジック（計算や判定）だけを残せばOK！
//    public boolean hasSpecificOptions() {
//        if (variants == null || variants.isEmpty()) return false;
//        return variants.stream()
//                .anyMatch(v -> v.getSize() != null || v.getColor() != null);
//    }
//}


package com.example.demo.models;
import java.util.List; // 追加
import javax.validation.constraints.Min;//マイナスの金額にならないように

import java.util.ArrayList; // 追加
import javax.persistence.*;
import com.example.demo.models.Favorite;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 500)
    private String description;
    
 // 【追加】お気に入りとのリレーション設定
    // cascade = CascadeType.ALL をつけることで、商品削除時にお気に入りデータも自動削除されます
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Favorite> favorites = new ArrayList<>();
    
    // バリエーションとの1対多のリレーション
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ProductVariant> variants = new ArrayList<>();

    @Min(value = 0, message = "価格は0円以上に設定してください")//価格（price）が0より小さくなったら、『ダメだよ！』と警告してね」 というルールを自動で実行してくれます。
    private int price;
    // コンストラクタ
    public Product() {}

    // Getter & Setter (variantsを追加)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getPrice() { return price; }
    public void setPrice(Integer price) { this.price = price; }

    public List<ProductVariant> getVariants() { return variants; } // 追加
    public void setVariants(List<ProductVariant> variants) { this.variants = variants; } // 追加
    
    
 // お気に入りです
    public List<Favorite> getFavorites() {
        return favorites;
    }

    public void setFavorites(List<Favorite> favorites) {
        this.favorites = favorites;
    }
 // サイズまたはカラーが1つでも設定されているか判定する
    public boolean hasSpecificOptions() {
        if (variants == null || variants.isEmpty()) {
            return false;
        }
        // 全てのバリエーションをチェックし、サイズかカラーが入っているものが1つでもあれば true
        for (ProductVariant v : variants) {
            if (v.getSize() != null || v.getColor() != null) {
                return true;
            }
        }
        return false;
    }
}