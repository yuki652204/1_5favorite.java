package com.example.demo.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.models.ProductVariant;

public interface ProductVariantRepository
	extends JpaRepository<ProductVariant, Long> {

	// 商品IDでバリエーション一覧取得
	List<ProductVariant> findByProductId(Long productId);
}
