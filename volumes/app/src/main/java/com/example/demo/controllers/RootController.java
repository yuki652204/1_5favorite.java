package com.example.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.demo.models.InquiryForm;
import com.example.demo.repositories.InquiryRepository;
import com.example.demo.repositories.ProductRepository;

@Controller
@RequestMapping("/")
public class RootController {

	@Autowired
	InquiryRepository repository; // お問い合わせ用リポジトリ

	@Autowired
	ProductRepository productRepository; // 商品用リポジトリ
	
	// ホーム画面
	@GetMapping
	public String index() {
		return "root/index";
	}


	// --- お問い合わせフォーム1 ---
	@GetMapping("/form")
	public String form(InquiryForm inquiryForm) {
		return "root/form";
	}

	@PostMapping("/form")
	public String form(@Validated InquiryForm inquiryForm, BindingResult bindingResult, Model model) {
		if (bindingResult.hasErrors()) {
			return "root/form";
		}
		repository.saveAndFlush(inquiryForm);
		inquiryForm.clear();
		model.addAttribute("message", "お問い合わせを受け付けました。");
		return "root/form";
	}

	// --- お問い合わせフォーム2 ---
	@GetMapping("/form2")
	public String form2(InquiryForm inquiryForm) {
		return "root/form2";
	}

	@PostMapping("/form2")
	public String form2(@Validated InquiryForm inquiryForm, BindingResult bindingResult, Model model) {
		if (bindingResult.hasErrors()) {
			return "root/form2";
		}
		repository.saveAndFlush(inquiryForm);
		inquiryForm.clear();
		model.addAttribute("message", "お問い合わせ2を受け付けました。");
		return "root/form2";
	}
	
}