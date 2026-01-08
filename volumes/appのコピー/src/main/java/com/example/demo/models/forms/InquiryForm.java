package com.example.demo.models.forms;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class InquiryForm {

    @NotBlank(message = "名前は必須です")
    @Size(max = 10, message = "名前は10文字以内で入力してください")
    private String name;

    @NotBlank(message = "メールアドレスは必須です")
    @Email(message = "メールアドレスの形式が正しくありません")
    private String mail;

    @NotBlank(message = "お問い合わせ内容は必須です")
    @Size(max = 400, message = "400文字以内で入力してください")
    private String content;

    // --- getter / setter ---

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    // フォーム初期化
    public void clear() {
        this.name = null;
        this.mail = null;
        this.content = null;
    }
}
