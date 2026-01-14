package com.example.demo.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {
////管理者用
//    @GetMapping("/login")
//    public String login() {
//        return "admin/login"; // templates/login.html を表示
//    }
    
 // ユーザー用から共通にした
    @GetMapping("/login")
    
    public String userLogin() {
        // templates/login.html を探しにいく
        return "user/login"; 
    }
}