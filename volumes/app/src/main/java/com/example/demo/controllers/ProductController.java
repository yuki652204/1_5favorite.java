package com.example.demo.controllers;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.example.demo.models.Product;
import com.example.demo.models.User;
import com.example.demo.repositories.FavoriteRepository;
import com.example.demo.repositories.ProductRepository;
import com.example.demo.repositories.UserRepository;
import com.example.demo.services.FavoriteService;

@Controller
@RequestMapping("/shop")
public class ProductController {

	private final ProductRepository productRepository;
	private final FavoriteService favoriteService;
	private final UserRepository userRepository;
	private final FavoriteRepository favoriteRepository;

	public ProductController(ProductRepository productRepository,
			FavoriteService favoriteService,
			UserRepository userRepository,
			FavoriteRepository favoriteRepository) {
		this.productRepository = productRepository;
		this.favoriteService = favoriteService;
		this.userRepository = userRepository;
		this.favoriteRepository = favoriteRepository;
	}

	// 商品一覧表示（ユーザー向け）
	@GetMapping({"", "/item"})
	public String listProducts(Model model) {//listProducts(Model model) メソッドの先頭で処理が止まる
		Long userId = 1L; // userId = 1L が変数に代入される
		List<Product> products = productRepository.findAll();//findAll() が実行され、SQL（SELECT * FROM products）が発行される。
		//変数 products に、データベース内の全商品データが入る
		Set<Long> favoriteProductIds = favoriteRepository.findByUserId(userId).stream()
				//お気に入りID」のセット（Set<Long>）が作成
				.map(f -> f.getProduct().getId())//お気に入り済み」かどうかを判定
				.collect(Collectors.toSet());//HTML（Thymeleaf）に送る準備
		// 【重要】nullチェックを追加して、必ず空のセットを入れる
		if (favoriteProductIds == null) {
		    favoriteProductIds = new java.util.HashSet<>();
		}
		model.addAttribute("products", products);
		model.addAttribute("favoriteProductIds", favoriteProductIds);
		model.addAttribute("userId", userId);
		//Javaから渡された products の数だけ <tr> タグを繰り返す
		//${product.name} などが、実際の「商品名（Tシャツなど）」に置き換わる
		//完成したHTMLがブラウザに返され、画面が表示される
		return "products/item"; // templates/products/item.html
	}

	// お気に入り追加
	@PostMapping("/favorite/add")
	public String addFavorite(@RequestParam Long userId,
			@RequestParam Long productId) {
		User user = userRepository.findById(userId).orElseThrow();
		Product product = productRepository.findById(productId).orElseThrow();
		favoriteService.addFavorite(user, product);
		return "redirect:/shop/item";
	}

	// お気に入り削除
	@PostMapping("/favorite/remove")
	public String removeFavorite(@RequestParam Long userId,
			@RequestParam Long productId) {
		User user = userRepository.findById(userId).orElseThrow();
		Product product = productRepository.findById(productId).orElseThrow();
		favoriteService.removeFavorite(user, product);
		return "redirect:/shop/item";
	}
}
