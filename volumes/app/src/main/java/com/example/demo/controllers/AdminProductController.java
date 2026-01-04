package com.example.demo.controllers;

import org.springframework.web.bind.WebDataBinder;

import org.springframework.web.bind.annotation.InitBinder;
import java.beans.PropertyEditorSupport;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.example.demo.repositories.ProductVariantRepository;

import com.example.demo.models.Product;
import com.example.demo.models.ProductVariant;
import com.example.demo.repositories.InquiryRepository;
import com.example.demo.repositories.ProductRepository;
import com.example.demo.repositories.ColorRepository;
import com.example.demo.repositories.SizeRepository;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.example.demo.repositories.FavoriteRepository; // 追加

@Controller
@RequestMapping("/admin/products")
public class AdminProductController {

	// 1. finalでスッキリした宣言にする
	private final ProductRepository productRepository;
	private final InquiryRepository inquiryRepository;
	private final ColorRepository colorRepository;
	private final SizeRepository sizeRepository;
	private final ProductVariantRepository productVariantRepository;
	private final FavoriteRepository favoriteRepository; // ★追加

	// 2. コンストラクタも短く書く
	public AdminProductController(
			ProductRepository productRepository,
			InquiryRepository inquiryRepository,
			ColorRepository colorRepository,
			SizeRepository sizeRepository,
			ProductVariantRepository productVariantRepository,
			FavoriteRepository favoriteRepository // ★追加
			) {
		this.productRepository = productRepository;
		this.inquiryRepository = inquiryRepository;
		this.colorRepository = colorRepository;
		this.sizeRepository = sizeRepository;
		this.productVariantRepository = productVariantRepository;
		this.favoriteRepository = favoriteRepository; // ★追加
	}

	// ...以下メソッド


	//ここから
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		// Size型への変換で空文字が来たらnullにする
		binder.registerCustomEditor(com.example.demo.models.Size.class, new PropertyEditorSupport() {
			@Override
			public void setAsText(String text) {
				if (text == null || text.isEmpty()) {
					setValue(null);
				} else {
					// 本来はRepositoryで検索すべきですが、IDだけセットしたオブジェクトを渡せばJPAが処理します
					com.example.demo.models.Size size = new com.example.demo.models.Size();
					size.setId(Long.parseLong(text));
					setValue(size);
				}
			}
		});

		// Color型も同様
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
	}//ここまで
	// 管理画面一覧 (http://localhost:8080/admin/products)
	//お問い合わせ内容と商品管理画面
	@GetMapping({ "", "/list" }) // "" は /admin/products と同義
	public String index(Model model) {
		
		
		
		model.addAttribute("products", productRepository.findAll());
		model.addAttribute("inquiries", inquiryRepository.findAll());
		return "admin/list";
	}

	// 新規作成画面
	@GetMapping("/new")
	public String newProduct(Model model) {
		Product product = new Product();
		ProductVariant firstVariant = new ProductVariant();
		firstVariant.setStock(0); // 初期値を0にセット
		product.getVariants().add(new ProductVariant());

		var allSizes = sizeRepository.findAll();
		var allColors = colorRepository.findAll();

		// ★重要：ここでコンソールに件数を出力する
		System.out.println("----- DEBUG START -----");
		System.out.println("Sizeの取得件数: " + allSizes.size());
		System.out.println("Colorの取得件数: " + allColors.size());
		System.out.println("----- DEBUG END -----");
		model.addAttribute("product", product);

		// ★追加：画面のプルダウン用に全リストを渡す
		model.addAttribute("colors", colorRepository.findAll());
		model.addAttribute("sizes", sizeRepository.findAll());

		return "products/edit"; // edit.html を使いまわす
	}
	//新規ほぞん
	@PostMapping("/save")
	public String saveProduct(@Validated @ModelAttribute Product product, BindingResult result, Model model, RedirectAttributes redirectAttributes) {

	    // 1. バリデーションチェック（価格マイナスなど）
	    if (result.hasErrors()) {
	        model.addAttribute("colors", colorRepository.findAll());
	        model.addAttribute("sizes", sizeRepository.findAll());
	        return "products/edit"; 
	    }

	    // ★重要：ここを追加！親子関係の紐付け
	    // 保存する前に、各バリエーションに「あなたの親はこの商品ですよ」とセットする
	    if (product.getVariants() != null) {
	        for (ProductVariant v : product.getVariants()) {
	            v.setProduct(product);
	        }
	    }

	    // 2. 保存
	    productRepository.save(product);

	    // 3. メッセージをセット
	    redirectAttributes.addFlashAttribute("message", "商品を新規保存しました！");

	    return "redirect:/admin/products";
	}

	// 商品削除
	@PostMapping("/delete/{id}")
	public String deleteProduct(@PathVariable Long id, RedirectAttributes redirectAttributes) {
		// ★重要：削除前にお気に入りの登録件数をチェック
        long favoriteCount = favoriteRepository.countByProductId(id);
        System.out.println("--- 削除チェック開始 ---");
        System.out.println("対象商品ID: " + id);
        System.out.println("お気に入り件数: " + favoriteCount);
        if (favoriteCount > 0) {
            // お気に入りがある場合は削除を中止し、エラーメッセージを送る
            redirectAttributes.addFlashAttribute("errorMessage", "この商品はお気に入り登録されているため、削除できません。");
            return "redirect:/admin/products";
        }

        // お気に入りがない場合のみ実行
		productRepository.deleteById(id);
		redirectAttributes.addFlashAttribute("message", "商品を削除しました！");
		return "redirect:/admin/products";
	}
	
	// 編集画面表示
	@GetMapping("/edit/{id}")
	public String edit(@PathVariable Long id, Model model) {

		Product product = productRepository.findById(id).orElse(null);

		if (product == null) {
			// 一覧に戻す（or エラーメッセージ表示）
			return "redirect:/admin/products?error=notfound";
		}

		model.addAttribute("product", product);
		model.addAttribute("colors", colorRepository.findAll());
		model.addAttribute("sizes", sizeRepository.findAll());
		return "products/edit";
	}


	// 更新処理
	@PostMapping("/update/{id}")
	public String update(@PathVariable Long id, @Validated @ModelAttribute Product product, BindingResult result, Model model, RedirectAttributes redirectAttributes) {
	    
	    // 1. バリデーションチェックを追加！
	    if (result.hasErrors()) {
	        model.addAttribute("colors", colorRepository.findAll());
	        model.addAttribute("sizes", sizeRepository.findAll());
	        return "products/edit"; // ここも products/edit に統一
	    }

	    product.setId(id);

	    // バリエーションの紐付け
	    if (product.getVariants() != null) {
	        for (ProductVariant v : product.getVariants()) {
	            v.setProduct(product);
	        }
	    }

	    productRepository.save(product);
	    redirectAttributes.addFlashAttribute("message", "商品を更新しました！");
	    return "redirect:/admin/products";
	}

	// お問い合わせの削除処理
	@PostMapping("/inquiry/delete/{id}")
	public String deleteInquiry(@PathVariable Long id) {
		// 正しく inquiryRepository を使う
		inquiryRepository.deleteById(id);

		// 削除後、管理画面のトップ（/admin/products）にリダイレクト
		return "redirect:/admin/products";
	}
}