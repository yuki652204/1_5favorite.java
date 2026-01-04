package com.example.demo.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.demo.models.Product;
import com.example.demo.models.User;
import com.example.demo.repositories.ProductRepository;
import com.example.demo.services.FavoriteService;

@Controller
@RequestMapping("/favorite")
public class FavoriteController {

    private final FavoriteService favoriteService;
    private final ProductRepository productRepository;

    public FavoriteController(
            FavoriteService favoriteService,
            ProductRepository productRepository) {
        this.favoriteService = favoriteService;
        this.productRepository = productRepository;
    }

    // ※ 仮ユーザー（後でログインユーザーに置き換える）
    private User getLoginUser() {
        User user = new User();
        user.setId(1L);
        return user;
    }

    @PostMapping("/{productId}/add")
    public String add(@PathVariable Long productId) {

        Product product = productRepository
            .findById(productId)
            .orElseThrow();

        favoriteService.addFavorite(getLoginUser(), product);

        return "redirect:/shop/item";
    }

    @PostMapping("/{productId}/remove")
    public String remove(@PathVariable Long productId) {

        Product product = productRepository
            .findById(productId)
            .orElseThrow();

        favoriteService.removeFavorite(getLoginUser(), product);

        return "redirect:/shop/item";
    }
}
