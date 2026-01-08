package com.example.demo.controllers;

/* ---------------------------------------------------------
 * Spring MVC（Webの仕組み）に関するインポート
 * --------------------------------------------------------- */
// このクラスを、ブラウザからのリクエストを受け付ける「窓口」にするための魔法の言葉
import org.springframework.stereotype.Controller; 

// URLの途中に含まれるID（例：/favorite/10/add の "10"）を抜き取って変数に入れるために使用
import org.springframework.web.bind.annotation.PathVariable; 

// フォームからデータが送られてきた時（保存や削除の実行ボタン）に反応するための設定
import org.springframework.web.bind.annotation.PostMapping; 

// クラス全体のURLの「住所の番地（/favorite）」を固定するために使用
import org.springframework.web.bind.annotation.RequestMapping; 

// 画面を表示するだけのリクエスト（ページを開く時）に反応するための設定
import org.springframework.web.bind.annotation.GetMapping; 

// Javaのプログラムから、HTML（Thymeleaf）に「データを渡すための箱」
import org.springframework.ui.Model; 


/* ---------------------------------------------------------
 * Javaの標準的な「データの集まり」に関するインポート
 * --------------------------------------------------------- */
// 商品を「並べて管理」するためのリスト構造（配列のようなもの）を使うために必要
import java.util.List;  

// 「お気に入り済みID」を、重複なく・高速に検索できる形式で保持するために必要
import java.util.Set;   


/* ---------------------------------------------------------
 * データベース操作（Repository）に関するインポート
 * --------------------------------------------------------- */
// 商品テーブル(MySQL)に対して、IDで検索したり保存したりする直接の窓口
import com.example.demo.repositories.ProductRepository; 


/* ---------------------------------------------------------
 * データの設計図（Model / Entity）に関するインポート
 * --------------------------------------------------------- */
// 「商品とは何か（ID、名前、価格）」というデータの定義を使用するために必要
import com.example.demo.models.Product; 

// 「ユーザーとは何か（ID、名前）」というデータの定義を使用するために必要
import com.example.demo.models.User; 


/* ---------------------------------------------------------
 * 業務ルール（Service）に関するインポート
 * --------------------------------------------------------- */
// 「お気に入り登録する時は重複チェックをする」などの具体的なルールが書かれた部品を呼び出す
import com.example.demo.services.FavoriteService; 

// 「全商品を取得する」「特定の条件で探す」などの商品に関するルールを呼び出す
import com.example.demo.services.ProductService;

/**
 * お気に入り機能の司令塔（Controller）
 * ユーザーの「ボタン操作」を、適切な「ビジネスロジック（Service）」へ繋ぐ役割を持ちます。
 */
@Controller 
@RequestMapping("/favorite") // このクラスのメソッドはすべて URL が "/favorite" から始まります
public class FavoriteController {

    // 【依存性の注入：DI】
    // 自分でインスタンス化（new）せず、Springが持っている部品（Bean）を借りてきます。
    private final FavoriteService favoriteService; // お気に入りの登録・削除の仕事用
    private final ProductRepository productRepository; // データベースから商品を探す用
    private final ProductService productService; // 商品一覧の取得や複雑な処理用

    /**
     * コンストラクタ（部品の組み立て）
     * Springが起動時に、必要な3つの部品をここに自動で差し込んでくれます。
     */
    public FavoriteController(
            FavoriteService favoriteService,
            ProductRepository productRepository,
            ProductService productService) {
        this.favoriteService = favoriteService;
        this.productRepository = productRepository;
        this.productService = productService;
    }
    
    /**
     * 商品一覧ページを表示する（GETリクエスト）
     * URL: GET /favorite/shop/item
     */
    @GetMapping("/shop/item")
    public String listItems(Model model) {
        // 1. 全商品のリストを取得（画面に並べるため）
        List<Product> products = productService.getAllProducts();
        
        // 2. 「products」という名前で、HTML（Thymeleaf）へデータを渡す
        model.addAttribute("products", products);

        // 3. 現在のユーザー(ID:1)が既にお気に入りにしている商品のIDだけを抽出
        // これにより、HTML側で「★」か「☆」かを判定できるようになります
        Set<Long> favoriteProductIds = favoriteService.getFavoriteProductIds(1L);
        model.addAttribute("favoriteProductIds", favoriteProductIds);

        // templates/admin/list.html を表示しなさいという命令
        return "admin/list"; 
    }

    /**
     * ログインユーザーの代わり（スタブ）
     * 本来はSpring Securityから取得しますが、今はテスト用にID:1のユーザーを生成します
     */
    private User getLoginUser() {
        User user = new User(); 
        user.setId(1L); 
        return user; 
    }

    /**
     * お気に入りに追加する処理（POSTリクエスト）
     * URL: POST /favorite/{productId}/add
     */
    @PostMapping("/{productId}/add")
    public String add(@PathVariable Long productId) {
        // 1. URLに含まれるIDを使って、DBから商品データを1件特定する
        Product product = productRepository
            .findById(productId)
            .orElseThrow(); // 見つからなければエラーを投げる

        // 2. FavoriteServiceに「このユーザーが、この商品をお気に入りしたよ」と命令する
        favoriteService.addFavorite(getLoginUser(), product);

        // 3. 処理が終わったら、商品一覧ページへ強制移動（リダイレクト）させて画面を更新する
        return "redirect:/shop/item";
    }

    /**
     * お気に入りから削除する処理（POSTリクエスト）
     * URL: POST /favorite/{productId}/remove
     */
    @PostMapping("/{productId}/remove")
    public String remove(@PathVariable Long productId) {
        // 1. 削除対象の商品を特定
        Product product = productRepository
            .findById(productId)
            .orElseThrow();

        // 2. Serviceに「お気に入りを解除して」と命令
        favoriteService.removeFavorite(getLoginUser(), product);

        // 3. 一覧ページへ戻す
        return "redirect:/shop/item";
    }
}