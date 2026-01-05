package com.example.demo.controllers;

import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import java.beans.PropertyEditorSupport;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.example.demo.models.Product;
import com.example.demo.models.ProductVariant;
import com.example.demo.repositories.InquiryRepository;
import com.example.demo.repositories.ProductRepository;
import com.example.demo.repositories.ColorRepository;
import com.example.demo.repositories.SizeRepository;
import com.example.demo.services.ProductService; // 追加
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/products")
public class AdminProductController {

    private final ProductRepository productRepository;
    private final InquiryRepository inquiryRepository;
    private final ColorRepository colorRepository;
    private final SizeRepository sizeRepository;
    private final ProductService productService; // Serviceを導入

    public AdminProductController(
            ProductRepository productRepository,
            InquiryRepository inquiryRepository,
            ColorRepository colorRepository,
            SizeRepository sizeRepository,
            ProductService productService
            ) {
        this.productRepository = productRepository;
        this.inquiryRepository = inquiryRepository;
        this.colorRepository = colorRepository;
        this.sizeRepository = sizeRepository;
        this.productService = productService;
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(com.example.demo.models.Size.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) {
                if (text == null || text.isEmpty()) {
                    setValue(null);
                } else {
                    com.example.demo.models.Size size = new com.example.demo.models.Size();
                    size.setId(Long.parseLong(text));
                    setValue(size);
                }
            }
        });

        binder.registerCustomEditor(com.example.demo.models.Color.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) {
                if (text == null || text.isEmpty()) {
                    setValue(null);
                } else {
                    com.example.demo.models.Color color = new com.example.demo.models.Color();
                    color.setId(Long.parseLong(text));
                    setValue(color);
                }
            }
        });
    }

    @GetMapping({ "", "/list" })
    public String index(Model model) {
        model.addAttribute("products", productRepository.findAll());
        model.addAttribute("inquiries", inquiryRepository.findAll());
        return "admin/list";
    }

    @GetMapping("/new")
    public String newProduct(Model model) {
        Product product = new Product();
        product.getVariants().add(new ProductVariant());
        model.addAttribute("product", product);
        model.addAttribute("colors", colorRepository.findAll());
        model.addAttribute("sizes", sizeRepository.findAll());
        return "products/edit";
    }

    @PostMapping("/save")
    public String saveProduct(@Validated @ModelAttribute Product product, BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("colors", colorRepository.findAll());
            model.addAttribute("sizes", sizeRepository.findAll());
            return "products/edit"; 
        }
        productService.saveWithVariants(product);
        redirectAttributes.addFlashAttribute("message", "商品を新規保存しました！");
        return "redirect:/admin/products";
    }

    @PostMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        if (!productService.deleteProductWithCheck(id)) {
            redirectAttributes.addFlashAttribute("errorMessage", "この商品はお気に入り登録されているため、削除できません。");
        } else {
            redirectAttributes.addFlashAttribute("message", "商品を削除しました！");
        }
        return "redirect:/admin/products";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        Product product = productRepository.findById(id).orElse(null);
        if (product == null) {
            return "redirect:/admin/products?error=notfound";
        }
        model.addAttribute("product", product);
        model.addAttribute("colors", colorRepository.findAll());
        model.addAttribute("sizes", sizeRepository.findAll());
        return "products/edit";
    }

    @PostMapping("/update/{id}")
    public String update(@PathVariable Long id, @Validated @ModelAttribute Product product, BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("colors", colorRepository.findAll());
            model.addAttribute("sizes", sizeRepository.findAll());
            return "products/edit";
        }
        product.setId(id);
        productService.saveWithVariants(product);
        redirectAttributes.addFlashAttribute("message", "商品を更新しました！");
        return "redirect:/admin/products";
    }

    @PostMapping("/inquiry/delete/{id}")
    public String deleteInquiry(@PathVariable Long id) {
        inquiryRepository.deleteById(id);
        return "redirect:/admin/products";
    }
}