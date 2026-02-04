package com.example.demo.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    // 管理者用ログイン画面の表示
    @GetMapping("/admin/login")
    public String adminLogin() {
        return "admin/ad.login"; // src/main/resources/templates/admin/login.html を表示
    }

    // 管理者ログイン後の画面（リスト画面など）
    @GetMapping("/admin/list")
    public String adminList() {
        return "admin/list"; // .html は不要です！
    }

    // ユーザー用ログイン画面の表示
    @GetMapping("/user/login")
    public String userLogin() {
        return "user/login"; // src/main/resources/templates/user/login.html を表示
    }

    // ユーザーログイン後の画面（ホーム画面）
    @GetMapping("/user/home")
    public String userHome() {
        return "root/index"; // src/main/resources/templates/user/home.html を表示
    }
}