package com.example.demo.controllers;

import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import java.beans.PropertyEditorSupport;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.example.demo.models.Product;
import com.example.demo.models.ProductVariant;
import com.example.demo.repositories.InquiryRepository;
import com.example.demo.repositories.ProductRepository;
import com.example.demo.repositories.ColorRepository;
import com.example.demo.repositories.SizeRepository;
import com.example.demo.services.ProductService; 
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/products")
public class AdminProductController {

    // 依存関係をフィールドで定義。Service層を導入しControllerの責務を軽減
    private final ProductRepository productRepository;
    private final InquiryRepository inquiryRepository;
    private final ColorRepository colorRepository;
    private final SizeRepository sizeRepository;
    private final ProductService productService; 

    // コンストラクタインジェクション（推奨される依存性注入の方法）
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
     * フォームから送信されたIDをオブジェクト（Size/Color）に変換する設定
     */
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(com.example.demo.models.Size.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) {
                if (text == null || text.isEmpty()) {
                    setValue(null);
                } else {
                    com.example.demo.models.Size size = new com.example.demo.models.Size();
                    size.setId(Long.parseLong(text));
                    setValue(size);
                }
            }
        });

        binder.registerCustomEditor(com.example.demo.models.Color.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) {
                if (text == null || text.isEmpty()) {
                    setValue(null);
                } else {
                    com.example.demo.models.Color color = new com.example.demo.models.Color();
                    color.setId(Long.parseLong(text));
                    setValue(color);
                }
            }
        });
    }

    /**
     * 商品一覧およびお問い合わせ一覧の表示
     */
    @GetMapping({ "", "/list" })
    public String index(Model model) {
        model.addAttribute("products", productRepository.findAll());
        model.addAttribute("inquiries", inquiryRepository.findAll());
        return "admin/list";
    }

    /**
     * 新規商品登録画面の表示
     */
    @GetMapping("/new")
    public String newProduct(Model model) {
        Product product = new Product();
        // 初期のバリエーション行を1つ追加
        product.getVariants().add(new ProductVariant());
        
        model.addAttribute("product", product);
        model.addAttribute("colors", colorRepository.findAll());
        model.addAttribute("sizes", sizeRepository.findAll());
        
        // デバッグ用：プルダウン用データが取得できているか確認
        System.out.println("New Product: Colors " + colorRepository.count() + ", Sizes " + sizeRepository.count());
        
        return "products/edit";
    }

    /**
     * 商品の新規保存処理
     * ビジネスロジックをProductServiceに委譲し、Controllerを簡潔に維持
     */
    @PostMapping("/save")
    public String saveProduct(@Validated @ModelAttribute Product product, BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("colors", colorRepository.findAll());
            model.addAttribute("sizes", sizeRepository.findAll());
            return "products/edit"; 
        }
        
        // Service層で親子関係（ProductとVariant）の紐付けと保存を実行
        productService.saveWithVariants(product);
        
        redirectAttributes.addFlashAttribute("message", "商品を新規保存しました！");
        return "redirect:/admin/products";
    }

    /**
     * 商品削除処理
     * 参照整合性チェック（お気に入り登録があるか）をService層で行う
     */
    @PostMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        // Serviceからの戻り値（boolean）で削除成功・失敗を判定
        if (!productService.deleteProductWithCheck(id)) {
            redirectAttributes.addFlashAttribute("errorMessage", "この商品はお気に入り登録されているため、削除できません。");
        } else {
            redirectAttributes.addFlashAttribute("message", "商品を削除しました！");
        }
        return "redirect:/admin/products";
    }

    /**
     * 商品編集画面の表示
     */
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        Product product = productRepository.findById(id).orElse(null);
        if (product == null) {
            return "redirect:/admin/products?error=notfound";
        }
        model.addAttribute("product", product);
        model.addAttribute("colors", colorRepository.findAll());
        model.addAttribute("sizes", sizeRepository.findAll());
        return "products/edit";
    }

    /**
     * 商品情報の更新処理
     */
    @PostMapping("/update/{id}")
    public String update(@PathVariable Long id, @Validated @ModelAttribute Product product, BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("colors", colorRepository.findAll());
            model.addAttribute("sizes", sizeRepository.findAll());
            return "products/edit";
        }
        product.setId(id);
        
        // 更新時もService層を利用して整合性を保つ
        productService.saveWithVariants(product);
        
        redirectAttributes.addFlashAttribute("message", "商品を更新しました！");
        return "redirect:/admin/products";
    }

    /**
     * お問い合わせの削除処理
     */
    @PostMapping("/inquiry/delete/{id}")
    public String deleteInquiry(@PathVariable Long id) {
        inquiryRepository.deleteById(id);
        return "redirect:/admin/products";
    }
}