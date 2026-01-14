package com.example.demo.services;

// 【重要】Productクラスは entity パッケージ配下にあるため、正しいパスでインポート
import com.example.demo.models.entity.Product;

// データの集まりを扱うための標準クラス
import java.util.Set;
import java.util.stream.Collectors;

// このクラスがサービス層（ビジネスロジックを担当）であることを宣言
import org.springframework.stereotype.Service;

// トランザクション管理（データベース操作の一貫性を保証）
import org.springframework.transaction.annotation.Transactional;

// モデルクラス
import com.example.demo.models.Favorite;
import com.example.demo.models.User;

// データベースアクセス用のリポジトリ
import com.example.demo.repositories.FavoriteRepository;
import com.example.demo.repositories.ProductRepository;
import com.example.demo.repositories.UserRepository;

/**
 * お気に入り機能のビジネスロジックを担当するサービスクラス
 * 
 * 【役割】
 * - お気に入りの登録・削除の処理
 * - 重複チェック（同じ商品を複数回お気に入りしないように）
 * - ユーザーのお気に入り商品ID一覧の取得
 * 
 * ControllerとRepositoryの間に位置し、複雑なビジネスルールをここに集約します。
 */
@Service // このクラスがサービス層であることをSpringに伝える
@Transactional // このクラスの全メソッドがトランザクション管理下で実行される
public class FavoriteService {
    
    // 【依存性注入用のフィールド】
    private final FavoriteRepository favoriteRepository; // お気に入りデータベースへのアクセス
    private final UserRepository userRepository;         // ユーザーデータベースへのアクセス
    private final ProductRepository productRepository;   // 商品データベースへのアクセス

    /**
     * コンストラクタインジェクション
     * Springが起動時に自動的に3つのRepositoryを渡してくれる
     * 
     * @param favoriteRepository お気に入りリポジトリ
     * @param userRepository ユーザーリポジトリ
     * @param productRepository 商品リポジトリ
     */
    public FavoriteService(FavoriteRepository favoriteRepository, 
                           UserRepository userRepository, 
                           ProductRepository productRepository) {
        this.favoriteRepository = favoriteRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    /**
     * IDを受け取ってお気に入り登録を実行する（Controllerから呼ばれる用）
     * 
     * 【処理の流れ】
     * 1. ユーザーIDから User エンティティを取得
     * 2. 商品IDから Product エンティティを取得
     * 3. エンティティを使って実際のお気に入り登録処理を呼び出す
     * 
     * @param userId お気に入りするユーザーのID
     * @param productId お気に入りする商品のID
     * @throws RuntimeException ユーザーまたは商品が見つからない場合
     */
    public void addFavorite(Long userId, Long productId) {
        // IDからエンティティを取得（見つからなければ例外を投げる）
        User user = userRepository.findById(userId).orElseThrow();
        Product product = productRepository.findById(productId).orElseThrow();
        
        // エンティティを渡してお気に入り登録を実行
        this.addFavorite(user, product);
    }

    /**
     * エンティティを受け取ってお気に入り登録を実行する
     * 
     * 【処理の流れ】
     * 1. 既にお気に入り登録されているかチェック
     * 2. 登録済みなら何もせず終了（重複防止）
     * 3. 未登録なら新規にお気に入りを作成して保存
     * 
     * @param user お気に入りするユーザー
     * @param product お気に入りする商品
     */
    public void addFavorite(User user, Product product) {
        // 既にこのユーザーがこの商品をお気に入りしているかチェック
        boolean exists = favoriteRepository
                .findByUserIdAndProductId(user.getId(), product.getId())
                .isPresent(); // Optional型の isPresent() でデータの有無を判定
        
        // 既に登録済みなら何もせず終了
        if (exists) {
            return; 
        }
        
        // 新しいお気に入りオブジェクトを作成
        Favorite favorite = new Favorite();
        favorite.setUser(user);       // ユーザーを設定
        favorite.setProduct(product); // 商品を設定
        
        // データベースに保存
        favoriteRepository.save(favorite);
    }

    /**
     * 指定したユーザーがお気に入り登録している商品のID一覧を取得する
     * 
     * 【処理の流れ】
     * 1. ユーザーIDでお気に入り一覧を取得
     * 2. 各お気に入りから商品IDだけを抽出
     * 3. Set（重複なしの集合）として返す
     * 
     * 【用途】
     * 商品一覧画面で「このユーザーが既にお気に入りしている商品」を判定するために使用
     * 
     * @param userId ユーザーID
     * @return お気に入り商品のIDのSet（重複なし）
     */
    @Transactional(readOnly = true) // 読み取り専用のトランザクション（パフォーマンス向上）
    public Set<Long> getFavoriteProductIds(Long userId) {
        return favoriteRepository.findByUserId(userId).stream()  // お気に入りのリストをStreamに変換
                .map(f -> f.getProduct().getId())                // 各お気に入りから商品IDを取り出す
                .collect(Collectors.toSet());                    // Setに変換して返す
    }

    /**
     * お気に入りを削除する
     * 
     * 【処理の流れ】
     * 1. ユーザーIDと商品IDでお気に入りを検索
     * 2. 見つかったら削除、見つからなければ何もしない
     * 
     * @param user お気に入りを解除するユーザー
     * @param product お気に入りを解除する商品
     */
    public void removeFavorite(User user, Product product) {
        favoriteRepository
            .findByUserIdAndProductId(user.getId(), product.getId()) // お気に入りを検索
            .ifPresent(favoriteRepository::delete);                  // 見つかったら削除
    }
}