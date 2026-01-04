package com.example.demo.models;

import java.io.Serializable;
import javax.persistence.*;

@Entity
@Table(name = "product_variants")
public class ProductVariant implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

 // 多 : 1 サイズ
    @ManyToOne
    @JoinColumn(name = "size_id", nullable = true)//サイズ・色が空でも保存可能になった
    private Size size;

    // 多 : 1 色
    @ManyToOne
    @JoinColumn(name = "color_id", nullable = true)//サイズ・色が空でも保存可能になった
    private Color color;

    @Column(nullable = false)
    private int stock;

    // コンストラクタ
    public ProductVariant() {}

    // 手動でGetter/Setter（
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public Size getSize() { return size; }
    public void setSize(Size size) { this.size = size; }

    public Color getColor() { return color; }
    public void setColor(Color color) { this.color = color; }

    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }
}