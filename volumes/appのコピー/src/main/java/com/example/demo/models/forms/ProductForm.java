package com.example.demo.models.forms;

import java.util.List;
import java.util.ArrayList;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import com.example.demo.models.ProductVariant;

public class ProductForm {
    private Long id;
    
    @NotBlank(message = "商品名を入力してください")
    private String name;

    @Min(value = 0, message = "価格は0円以上に設定してください")
    private Integer price; // 入力値が空の場合を考慮してInteger

    private String description;

    // 変数の定義が漏れていたので追加します
    private List<ProductVariant> variants = new ArrayList<>();

    // --- 以下、重複を整理した Getter & Setter ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getPrice() { return price; }
    public void setPrice(Integer price) { this.price = price; }

    public List<ProductVariant> getVariants() { return variants; }
    public void setVariants(List<ProductVariant> variants) { this.variants = variants; }
}