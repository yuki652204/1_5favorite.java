package com.example.demo.repositories;

// Spring Data JPAの基本機能（CRUD操作を自動生成してくれる）
import org.springframework.data.jpa.repository.JpaRepository;

// このインターフェースをRepositoryとしてSpringに登録
import org.springframework.stereotype.Repository;

// 【重要】Productクラスは entity パッケージ配下にあるため、正しいパスでインポート
import com.example.demo.models.entity.Product;

/**
 * 商品テーブル(products)へのアクセスを担当するインターフェース
 * 
 * JpaRepositoryを継承することで、以下のメソッドが自動で使えるようになります：
 * - save(Product product)          : 商品を保存（新規登録または更新）
 * - findById(Long id)              : IDで商品を1件取得
 * - findAll()                      : 全商品を取得
 * - deleteById(Long id)            : IDで商品を削除
 * - count()                        : 商品の総件数を取得
 * - existsById(Long id)            : 指定IDの商品が存在するかチェック
 * 
 * カスタムメソッドが必要な場合は、このインターフェース内にメソッド宣言を追加するだけで、
 * Spring Data JPAがメソッド名から自動的にSQL（JPQL）を生成してくれます。
 * 
 * 例：List<Product> findByNameContaining(String keyword);
 *     → 商品名に特定のキーワードを含む商品を検索
 */
@Repository // このインターフェースがRepositoryであることをSpringに伝える
public interface ProductRepository extends JpaRepository<Product, Long> {
    // JpaRepository<Product, Long> の意味：
    // - Product : このRepositoryが扱うエンティティ（モデル）の型
    // - Long    : エンティティの主キー（ID）の型
    
    // 基本的なCRUD操作は継承により自動で使えるため、ここには何も書く必要がない
    // カスタムクエリが必要な場合のみ、メソッドを追加する
}