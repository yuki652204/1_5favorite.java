package com.example.demo.controllers;

// Spring MVCの基本アノテーション
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

// 【重要】Productクラスは entity パッケージ配下にあるため、正しいパスでインポート
// （このControllerでは直接Productを扱っていないが、将来の拡張のために記載）
import com.example.demo.models.entity.Product;

// ビジネスロジックを担当するサービス
import com.example.demo.services.FavoriteService;
import com.example.demo.services.ProductService;

/**
 * 商品一覧画面（ユーザー向け）とお気に入り機能を担当するコントローラー
 * 
 * 【役割】
 * - 商品一覧ページの表示（お気に入り状態も含む）
 * - お気に入り追加処理
 * 
 * 【設計方針】
 * - Repositoryを直接使わず、Serviceを経由してデータアクセスすることで、
 *   ビジネスロジックを集約し、Controllerをシンプルに保つ
 */
@Controller // このクラスがSpring MVCのコントローラーであることを宣言
@RequestMapping("/shop") // このコントローラーの基本URLパスを指定
public class ProductController {

    // 【依存性注入用のフィールド】
    private final ProductService productService;     // 商品関連のビジネスロジック
    private final FavoriteService favoriteService;   // お気に入り関連のビジネスロジック

    /**
     * コンストラクタインジェクション
     * 必要なサービスだけを注入（Repositoryを直接持たなくて済む）
     * 
     * @param productService 商品サービス
     * @param favoriteService お気に入りサービス
     */
    public ProductController(ProductService productService, FavoriteService favoriteService) {
        this.productService = productService;
        this.favoriteService = favoriteService;
    }

    /**
     * 商品一覧ページを表示する
     * URL: GET /shop または GET /shop/item
     * 
     * 【処理の流れ】
     * 1. 全商品を取得
     * 2. 現在のユーザーがお気に入り登録している商品のID一覧を取得
     * 3. それらをモデルに追加して画面に渡す
     * 4. 画面側で「このユーザーがお気に入り済みか」を判定できる
     * 
     * @param model 画面にデータを渡すための箱
     * @return 表示するビュー名（templates/products/item.html）
     */
    @GetMapping({"", "/item"})
    public String listProducts(Model model) {
        // ログインユーザーのID（実際はSpring Securityから取得）
        // テスト用に固定値1Lを使用
        Long userId = 1L;

        // 全商品をService経由で取得し、モデルに追加
        // Controllerでは「何を表示するか」だけを指定し、
        // 「どうやって取得するか」はServiceに任せる
        model.addAttribute("products", productService.getAllProducts());

        // このユーザーが既にお気に入りしている商品のIDのSetを取得
        // 画面側で th:if="${favoriteProductIds.contains(product.id)}" のように判定できる
        model.addAttribute("favoriteProductIds", favoriteService.getFavoriteProductIds(userId));
        
        // ユーザーIDも画面に渡す（フォーム送信時に使用）
        model.addAttribute("userId", userId);

        // templates/products/item.html を表示
        return "products/item";
    }

    /**
     * お気に入り追加処理
     * URL: POST /shop/favorite/add
     * 
     * 【処理の流れ】
     * 1. フォームから送信されたユーザーIDと商品IDを受け取る
     * 2. Service層にお気に入り登録を依頼
     * 3. 処理完了後、商品一覧ページへリダイレクト
     * 
     * @param userId お気に入りするユーザーのID
     * @param productId お気に入りする商品のID
     * @return リダイレクト先（商品一覧ページ）
     */
    @PostMapping("/favorite/add")
    public String addFavorite(
            @RequestParam Long userId,    // フォームのname="userId"の値を受け取る
            @RequestParam Long productId) { // フォームのname="productId"の値を受け取る
        
        // ServiceにIDだけを渡して登録処理を依頼
        // 「どうやって登録するか」「重複チェックはどうするか」などの
        // 詳細なロジックはすべてService層が担当する
        favoriteService.addFavorite(userId, productId);
        
        // PRG（Post-Redirect-Get）パターン：
        // POST処理後は必ずリダイレクトすることで、ブラウザの「戻る」や
        // 「再読み込み」による二重送信を防ぐ
        return "redirect:/shop/item";
    }
}