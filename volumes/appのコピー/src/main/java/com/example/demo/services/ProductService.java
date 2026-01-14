package com.example.demo.services;

import java.util.List; // 追加：Listを使うために必要

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import com.example.demo.models.ProductVariant;
import com.example.demo.repositories.ProductRepository;
import com.example.demo.repositories.FavoriteRepository;
import com.example.demo.models.entity.Product;
@Service
@Transactional
public class ProductService {

    private final ProductRepository productRepository;
    private final FavoriteRepository favoriteRepository;

    public ProductService(ProductRepository productRepository, FavoriteRepository favoriteRepository) {
        this.productRepository = productRepository;
        this.favoriteRepository = favoriteRepository;
    }

 // ProductService.java に追加しておくと便利なメソッド
    @Transactional(readOnly = true)
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("商品が見つかりません: " + id));
    }
    /**
     * 追加：全商品を取得する
     * Controllerからの呼び出しに対応
     */
    @Transactional(readOnly = true)
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    /**
     * お気に入り登録されているかチェックして削除を実行
     * @return 削除成功ならtrue, お気に入りがあり不可ならfalse
     */
    public boolean deleteProductWithCheck(Long id) {
        long favoriteCount = favoriteRepository.countByProductId(id);
        if (favoriteCount > 0) {
            return false;
        }
        productRepository.deleteById(id);
        return true;
    }

    /**
     * 商品とバリエーションを紐付けて保存
     */
    public void saveWithVariants(Product product) {
        if (product.getVariants() != null) {
            for (ProductVariant v : product.getVariants()) {
                v.setProduct(product);
            }
        }
        productRepository.save(product);
    }
}