package com.example.demo.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.demo.models.Product;
import com.example.demo.models.ProductVariant;
import com.example.demo.repositories.ProductRepository;
import com.example.demo.repositories.FavoriteRepository;

@Service
@Transactional
public class ProductService {

    private final ProductRepository productRepository;
    private final FavoriteRepository favoriteRepository;

    public ProductService(ProductRepository productRepository, FavoriteRepository favoriteRepository) {
        this.productRepository = productRepository;
        this.favoriteRepository = favoriteRepository;
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