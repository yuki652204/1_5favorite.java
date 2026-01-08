package com.example.demo.repositories;

import org.springframework.data.jpa.repository.JpaRepository; // Spring Data JPAの基本機能
import org.springframework.stereotype.Repository;            // Repositoryとして登録
import com.example.demo.models.Product;                      // 商品モデルを扱う

/**
 * 商品テーブル(products)へのアクセスを担当するインターフェース
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    // JpaRepositoryを継承することで、以下のメソッドが自動で使えるようになります：
    // - save() : 保存
    // - findById() : 1件取得
    // - findAll() : 全件取得
    // - deleteById() : 削除
}