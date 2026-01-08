package com.example.demo.services;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.models.Favorite;
import com.example.demo.models.Product;
import com.example.demo.models.User;
import com.example.demo.repositories.FavoriteRepository;
import com.example.demo.repositories.ProductRepository; // 追加
import com.example.demo.repositories.UserRepository;    // 追加

@Service
@Transactional
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;       // 追加
    private final ProductRepository productRepository; // 追加

    // コンストラクタで3つのRepositoryを受け取るように修正
    public FavoriteService(FavoriteRepository favoriteRepository, 
                           UserRepository userRepository, 
                           ProductRepository productRepository) {
        this.favoriteRepository = favoriteRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    /**
     * IDを受け取ってお気に入り登録を実行する (Controllerから呼ばれる用)
     */
    public void addFavorite(Long userId, Long productId) {
        // IDからエンティティを取得
        User user = userRepository.findById(userId).orElseThrow();
        Product product = productRepository.findById(productId).orElseThrow();
        
        // 下にある既存の addFavorite(User, Product) を呼び出す
        this.addFavorite(user, product);
    }

    /**
     * エンティティを受け取ってお気に入り登録を実行する
     */
    public void addFavorite(User user, Product product) {
        boolean exists = favoriteRepository
                .findByUserIdAndProductId(user.getId(), product.getId())
                .isPresent();

        if (exists) {
            return; 
        }

        Favorite favorite = new Favorite();
        favorite.setUser(user);
        favorite.setProduct(product);

        favoriteRepository.save(favorite);
    }

    /**
     * お気に入りID一覧を取得する
     */
    @Transactional(readOnly = true)
    public Set<Long> getFavoriteProductIds(Long userId) {
        return favoriteRepository.findByUserId(userId).stream()
                .map(f -> f.getProduct().getId())
                .collect(Collectors.toSet());
    }

    /**
     * お気に入り削除
     */
    public void removeFavorite(User user, Product product) {
        favoriteRepository
            .findByUserIdAndProductId(user.getId(), product.getId())
            .ifPresent(favoriteRepository::delete);
    }
}