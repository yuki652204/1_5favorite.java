package com.example.demo.controllers;

import java.util.Set;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;

// 自作 Service と Model のインポート
import com.example.demo.services.ProductService;
import com.example.demo.services.FavoriteService;
import com.example.demo.models.Product;

@Controller
@RequestMapping("/shop")
public class ProductController {

    private final ProductService productService;
    private final FavoriteService favoriteService;

    // 必要なサービスだけを注入（Repositoryを直接持たなくて済む）
    public ProductController(ProductService productService, FavoriteService favoriteService) {
        this.productService = productService;
        this.favoriteService = favoriteService;
    }

    @GetMapping({"", "/item"})
    public String listProducts(Model model) {
        Long userId = 1L; // 実際はログインユーザーから取得

        // 全商品の取得もService経由にする（productServiceにfindAllがあると仮定）
        model.addAttribute("products", productService.getAllProducts());
        
        // Serviceを呼ぶだけ！「どうやってIDを取り出すか」はServiceが知っている
        model.addAttribute("favoriteProductIds", favoriteService.getFavoriteProductIds(userId));
        model.addAttribute("userId", userId);
        
        return "products/item";
    }

    // お気に入り登録もIDだけで指示を出す
    @PostMapping("/favorite/add")
    public String addFavorite(@RequestParam Long userId, @RequestParam Long productId) {
        favoriteService.addFavorite(userId, productId);
        return "redirect:/shop/item";
    }
}