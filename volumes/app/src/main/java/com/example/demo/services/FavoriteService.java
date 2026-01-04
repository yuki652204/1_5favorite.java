package com.example.demo.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.models.Favorite;
import com.example.demo.models.Product;
import com.example.demo.models.User;
import com.example.demo.repositories.FavoriteRepository;

@Service
@Transactional
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;

    public FavoriteService(FavoriteRepository favoriteRepository) {
        this.favoriteRepository = favoriteRepository;
    }

    // お気に入り追加
    public void addFavorite(User user, Product product) {
//① Controller から user と product を受け取る

//    	③ すでに存在 → 何もしない
//    	④ 存在しない → Favorite を作って保存
        // すでに登録されているか確認
//    	③ すでに存在 → 何もしない
//    	④ 存在しない → Favorite を作って保存
        boolean exists = favoriteRepository
                .findByUserIdAndProductId(user.getId(), product.getId())
                .isPresent();

        if (exists) {
            return; // すでにあれば何もしない
        }

        Favorite favorite = new Favorite();
        favorite.setUser(user);
        favorite.setProduct(product);

        favoriteRepository.save(favorite);
    }

    // お気に入り削除
    public void removeFavorite(User user, Product product) {

        favoriteRepository
            .findByUserIdAndProductId(user.getId(), product.getId())
            .ifPresent(favoriteRepository::delete);
    }
}
