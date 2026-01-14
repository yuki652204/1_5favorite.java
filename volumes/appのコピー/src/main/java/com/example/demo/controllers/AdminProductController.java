package com.example.demo.controllers;

// Spring MVCの基本機能をインポート
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;

// カスタムエディタ（フォームの値をオブジェクトに変換する機能）
import java.beans.PropertyEditorSupport;

// バリデーション（入力チェック）関連
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;

// Spring MVCの基本アノテーション
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

// 【重要】Productクラスは entity パッケージ配下にあるため、正しいパスでインポート
import com.example.demo.models.entity.Product;

// その他のモデルクラス
import com.example.demo.models.ProductVariant;

// データベースアクセス用のリポジトリ
import com.example.demo.repositories.InquiryRepository;
import com.example.demo.repositories.ProductRepository;
import com.example.demo.repositories.ColorRepository;
import com.example.demo.repositories.SizeRepository;

// ビジネスロジックを担当するサービス
import com.example.demo.services.ProductService; 

// リダイレクト時にメッセージを渡すための機能
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * 管理画面の商品管理を担当するコントローラー
 * 商品の一覧表示、新規登録、編集、削除を行う
 */
@Controller // このクラスがSpring MVCのコントローラーであることを宣言
@RequestMapping("/admin/products") // このコントローラーの基本URLパスを指定
public class AdminProductController {

    // 【依存性注入用のフィールド】
    // これらはコンストラクタでSpringが自動的に注入してくれる
    private final ProductRepository productRepository; // 商品データベースへのアクセス
    private final InquiryRepository inquiryRepository; // お問い合わせデータベースへのアクセス
    private final ColorRepository colorRepository;     // 色マスタデータへのアクセス
    private final SizeRepository sizeRepository;       // サイズマスタデータへのアクセス
    private final ProductService productService;       // 商品関連のビジネスロジック

    /**
     * コンストラクタインジェクション（推奨される依存性注入の方法）
     * Springが起動時に自動的に必要なインスタンスを渡してくれる
     */
    public AdminProductController(
            ProductRepository productRepository,
            InquiryRepository inquiryRepository,
            ColorRepository colorRepository,
            SizeRepository sizeRepository,
            ProductService productService
            ) {
        this.productRepository = productRepository;
        this.inquiryRepository = inquiryRepository;
        this.colorRepository = colorRepository;
        this.sizeRepository = sizeRepository;
        this.productService = productService;
    }

    /**
     * フォームから送信された文字列IDをオブジェクト（Size/Color）に変換する設定
     * HTMLフォームでは「1」という文字列で送られてくるが、Javaでは Size オブジェクトとして扱いたい場合に必要
     */
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        // Size用のカスタムエディタを登録
        binder.registerCustomEditor(com.example.demo.models.Size.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) {
                // 文字列が空の場合はnullを設定
                if (text == null || text.isEmpty()) {
                    setValue(null);
                } else {
                    // 文字列をLongに変換してSizeオブジェクトを作成
                    com.example.demo.models.Size size = new com.example.demo.models.Size();
                    size.setId(Long.parseLong(text));
                    setValue(size);
                }
            }
        });

        // Color用のカスタムエディタを登録
        binder.registerCustomEditor(com.example.demo.models.Color.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) {
                // 文字列が空の場合はnullを設定
                if (text == null || text.isEmpty()) {
                    setValue(null);
                } else {
                    // 文字列をLongに変換してColorオブジェクトを作成
                    com.example.demo.models.Color color = new com.example.demo.models.Color();
                    color.setId(Long.parseLong(text));
                    setValue(color);
                }
            }
        });
    }

    /**
     * 商品一覧およびお問い合わせ一覧の表示
     * URL: GET /admin/products または GET /admin/products/list
     */
    @GetMapping({ "", "/list" })
    public String index(Model model) {
        // データベースから全商品を取得してモデルに追加
        model.addAttribute("products", productRepository.findAll());
        // データベースから全お問い合わせを取得してモデルに追加
        model.addAttribute("inquiries", inquiryRepository.findAll());
        // templates/admin/list.html を表示
        return "admin/list";
    }

    /**
     * 新規商品登録画面の表示
     * URL: GET /admin/products/new
     */
    @GetMapping("/new")
    public String newProduct(Model model) {
        // 空の商品オブジェクトを作成
        Product product = new Product();
        // 初期のバリエーション行を1つ追加（画面でバリエーション入力欄を表示するため）
        product.getVariants().add(new ProductVariant());
        
        // 画面に渡すデータをモデルに追加
        model.addAttribute("product", product);
        model.addAttribute("colors", colorRepository.findAll());   // 色のプルダウン用データ
        model.addAttribute("sizes", sizeRepository.findAll());     // サイズのプルダウン用データ
        
        // デバッグ用：プルダウン用データが取得できているか確認
        System.out.println("New Product: Colors " + colorRepository.count() + ", Sizes " + sizeRepository.count());
        
        // templates/products/edit.html を表示
        return "products/edit";
    }

    /**
     * 商品の新規保存処理
     * URL: POST /admin/products/save
     * ビジネスロジックをProductServiceに委譲し、Controllerを簡潔に維持
     */
    @PostMapping("/save")
    public String saveProduct(
            @Validated @ModelAttribute Product product,  // フォームから送られたデータをProductオブジェクトに自動変換＆バリデーション
            BindingResult result,                         // バリデーション結果を格納
            Model model, 
            RedirectAttributes redirectAttributes) {      // リダイレクト先にメッセージを渡すための機能
        
        // バリデーションエラーがある場合
        if (result.hasErrors()) {
            // プルダウン用データを再度追加（画面再表示のため）
            model.addAttribute("colors", colorRepository.findAll());
            model.addAttribute("sizes", sizeRepository.findAll());
            // エラーメッセージと共に入力画面を再表示
            return "products/edit"; 
        }
        
        // Service層で親子関係（ProductとVariant）の紐付けと保存を実行
        productService.saveWithVariants(product);
        
        // 成功メッセージをフラッシュスコープに追加（リダイレクト後も1回だけ表示される）
        redirectAttributes.addFlashAttribute("message", "商品を新規保存しました!");
        // 商品一覧ページへリダイレクト
        return "redirect:/admin/products";
    }

    /**
     * 商品削除処理
     * URL: POST /admin/products/delete/{id}
     * 参照整合性チェック（お気に入り登録があるか）をService層で行う
     */
    @PostMapping("/delete/{id}")
    public String deleteProduct(
            @PathVariable Long id,                    // URLパスから商品IDを取得
            RedirectAttributes redirectAttributes) {  // リダイレクト先にメッセージを渡すための機能
        
        // Serviceからの戻り値（boolean）で削除成功・失敗を判定
        if (!productService.deleteProductWithCheck(id)) {
            // お気に入り登録されているため削除できない場合
            redirectAttributes.addFlashAttribute("errorMessage", "この商品はお気に入り登録されているため、削除できません。");
        } else {
            // 削除成功の場合
            redirectAttributes.addFlashAttribute("message", "商品を削除しました!");
        }
        // 商品一覧ページへリダイレクト
        return "redirect:/admin/products";
    }

    /**
     * 商品編集画面の表示
     * URL: GET /admin/products/edit/{id}
     */
    @GetMapping("/edit/{id}")
    public String edit(
            @PathVariable Long id,  // URLパスから商品IDを取得
            Model model) {
        
        // IDに該当する商品をデータベースから取得
        Product product = productRepository.findById(id).orElse(null);
        
        // 商品が見つからない場合はエラーパラメータ付きで一覧ページへリダイレクト
        if (product == null) {
            return "redirect:/admin/products?error=notfound";
        }
        
        // 編集画面に表示するデータをモデルに追加
        model.addAttribute("product", product);
        model.addAttribute("colors", colorRepository.findAll());
        model.addAttribute("sizes", sizeRepository.findAll());
        
        // templates/products/edit.html を表示
        return "products/edit";
    }

    /**
     * 商品情報の更新処理
     * URL: POST /admin/products/update/{id}
     */
    @PostMapping("/update/{id}")
    public String update(
            @PathVariable Long id,                        // URLパスから商品IDを取得
            @Validated @ModelAttribute Product product,   // フォームデータをProductオブジェクトに変換＆バリデーション
            BindingResult result,                         // バリデーション結果
            Model model, 
            RedirectAttributes redirectAttributes) {
        
        // バリデーションエラーがある場合
        if (result.hasErrors()) {
            // プルダウン用データを再度追加
            model.addAttribute("colors", colorRepository.findAll());
            model.addAttribute("sizes", sizeRepository.findAll());
            // エラーメッセージと共に編集画面を再表示
            return "products/edit";
        }
        
        // URLから取得したIDを商品オブジェクトに設定（更新対象を明確にする）
        product.setId(id);
        
        // 更新時もService層を利用して整合性を保つ
        productService.saveWithVariants(product);
        
        // 成功メッセージを追加
        redirectAttributes.addFlashAttribute("message", "商品を更新しました!");
        // 商品一覧ページへリダイレクト
        return "redirect:/admin/products";
    }

    /**
     * お問い合わせの削除処理
     * URL: POST /admin/products/inquiry/delete/{id}
     */
    @PostMapping("/inquiry/delete/{id}")
    public String deleteInquiry(@PathVariable Long id) {  // URLパスからお問い合わせIDを取得
        // お問い合わせをデータベースから削除
        inquiryRepository.deleteById(id);
        // 商品一覧ページへリダイレクト
        return "redirect:/admin/products";
    }
}