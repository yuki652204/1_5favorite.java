package com.example.demo.controllers; // このクラスが「コントローラー層」に属することを宣言

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.demo.models.forms.InquiryForm;
import com.example.demo.services.InquiryService;

@Controller // このクラスを「画面制御を行うコントローラー」としてSpringの管理下に置く
@RequestMapping("/form") // このクラス内のメソッドはすべてURLの "/form" から始まるリクエストを担当する
public class InquiryController {

    // ビジネスロジックを担当するServiceを保持するフィールド
    // finalをつけることで「後から書き換え不可」にし、安全性を高めている（プロの現場での推奨）
    private final InquiryService inquiryService;

   
    public InquiryController(InquiryService inquiryService) {
        this.inquiryService = inquiryService;
    }

    // --- お問い合わせ1 (/form) の表示処理 ---
    @GetMapping // URL "/form" に対して「GET」リクエスト（ページ表示）が来た時に実行
    public String form(InquiryForm inquiryForm) {
        // 引数に inquiryForm を入れることで、HTML側で空の入力用オブジェクトが使えるようになる
        return "root/form"; // templates/root/form.html を画面に表示する
    }

    // --- お問い合わせ1 (/form) の送信処理 ---
    @PostMapping // URL "/form" に対して「POST」リクエスト（データ送信）が来た時に実行
    public String submit(@Validated InquiryForm inquiryForm, BindingResult bindingResult, Model model) {
        // @Validated: 入力値が正しいかチェック（アノテーションに基づいたチェック）
        // BindingResult: チェック結果（エラーがあるかどうか）が格納される
        
        if (bindingResult.hasErrors()) {
            // もし入力エラー（必須漏れなど）があれば、保存せずに元の入力画面を再表示する
            return "root/form";
        }
        
        // エラーがない場合、Serviceを呼び出してデータベースへの保存処理を実行
        inquiryService.saveInquiry(inquiryForm);
        
        // 保存後にフォームの中身を空にする（再入力の準備）
        inquiryForm.clear();
        
        // 画面に表示する完了メッセージをセットする
        model.addAttribute("message", "お問い合わせを受け付けました。");
        
        return "root/form"; // メッセージを含めて入力画面を再表示する
    }

    // --- お問い合わせ2 (/form/form2) の表示処理 ---
    @GetMapping("/form2") // URL "/form/form2" に対して「GET」リクエストが来た時に実行
    public String form2(InquiryForm inquiryForm) {
        return "root/form2"; // templates/root/form2.html を表示
    }

    // --- お問い合わせ2 (/form/form2) の送信処理 ---
    @PostMapping("/form2") // URL "/form/form2" に対して「POST」リクエストが来た時に実行
    public String submit2(@Validated InquiryForm inquiryForm, BindingResult bindingResult, Model model) {
        
        if (bindingResult.hasErrors()) {
            // エラーがあればお問い合わせ2の画面に戻る
            return "root/form2";
        }
        
        // お問い合わせ1と同じ保存処理（ロジックを共通化している）を呼び出す
        inquiryService.saveInquiry(inquiryForm);
        
        inquiryForm.clear();
        
        // お問い合わせ2専用の完了メッセージをセット
        model.addAttribute("message", "お問い合わせ2を受け付けました。");
        
        return "root/form2"; // メッセージを含めて入力画面2を再表示する
    }
}