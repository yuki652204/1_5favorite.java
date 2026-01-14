package com.example.demo.models;

// シリアライズ（オブジェクトをバイト列に変換）するための機能
import java.io.Serializable;

// 【重要】Productクラスは entity パッケージ配下にあるため、正しいパスでインポート
import com.example.demo.models.entity.Product;

// JPA（Java Persistence API）のアノテーションをインポート
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * 商品バリエーション（在庫）のエンティティクラス
 * 1つの商品に対して、サイズや色の組み合わせごとに在庫を管理する
 * 例：Tシャツ（商品）→ 赤・Sサイズ（バリエーション1）、青・Mサイズ（バリエーション2）
 * データベースの「product_variants」テーブルと紐づく
 */
@Entity // このクラスがデータベースのテーブルと対応することを宣言
@Table(name = "product_variants") // データベースのテーブル名を指定
public class ProductVariant implements Serializable {

    // シリアライズのバージョン管理用ID
    private static final long serialVersionUID = 1L;

    // 【主キー（Primary Key）】
    @Id // この項目が主キーであることを宣言
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 自動採番（AUTO_INCREMENT）
    private Long id;

    // 【リレーション：多対一（バリエーション → 商品）】
    // 多くのバリエーションが1つの商品に紐づく
    @ManyToOne // 多対一の関係を定義
    @JoinColumn(name = "product_id", nullable = false) // 外部キー列名を指定、NULL不可（必ず商品に紐づく）
    private Product product;

    // 【リレーション：多対一（バリエーション → サイズ）】
    // 多くのバリエーションが1つのサイズに紐づく（サイズなしも許可）
    @ManyToOne // 多対一の関係を定義
    @JoinColumn(name = "size_id", nullable = true) // 外部キー列名を指定、NULL可（サイズが空でも保存可能）
    private Size size;

    // 【リレーション：多対一（バリエーション → 色）】
    // 多くのバリエーションが1つの色に紐づく（色なしも許可）
    @ManyToOne // 多対一の関係を定義
    @JoinColumn(name = "color_id", nullable = true) // 外部キー列名を指定、NULL可（色が空でも保存可能）
    private Color color;

    // 【在庫数】
    @Column(nullable = false) // NULL不可（在庫数は必須）
    private int stock;

    // --- コンストラクタ ---
    
    /**
     * デフォルトコンストラクタ
     * JPAがオブジェクトを生成する際に必要
     */
    public ProductVariant() {}

    // --- Getter（データを取得するメソッド）---
    
    /**
     * バリエーションIDを取得
     * @return バリエーションID
     */
    public Long getId() { 
        return id; 
    }
    
    /**
     * IDを設定
     * @param id バリエーションID
     */
    public void setId(Long id) { 
        this.id = id; 
    }

    /**
     * 紐づく商品を取得
     * @return 商品オブジェクト
     */
    public Product getProduct() { 
        return product; 
    }
    
    /**
     * 商品を設定
     * @param product 紐づける商品オブジェクト
     */
    public void setProduct(Product product) { 
        this.product = product; 
    }

    /**
     * サイズを取得
     * @return サイズオブジェクト（サイズなしの場合はnull）
     */
    public Size getSize() { 
        return size; 
    }
    
    /**
     * サイズを設定
     * @param size サイズオブジェクト（nullも可）
     */
    public void setSize(Size size) { 
        this.size = size; 
    }

    /**
     * 色を取得
     * @return 色オブジェクト（色なしの場合はnull）
     */
    public Color getColor() { 
        return color; 
    }
    
    /**
     * 色を設定
     * @param color 色オブジェクト（nullも可）
     */
    public void setColor(Color color) { 
        this.color = color; 
    }

    /**
     * 在庫数を取得
     * @return 在庫数
     */
    public int getStock() { 
        return stock; 
    }
    
    /**
     * 在庫数を設定
     * @param stock 在庫数
     */
    public void setStock(int stock) { 
        this.stock = stock; 
    }
}