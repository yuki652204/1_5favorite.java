package com.example.demo.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class RootController {

    /**
     * ホーム画面の表示
     * ルートパス ("/") へのアクセスを担当します。
     */
    @GetMapping
    public String index() {
        return "root/index";//index.htmlをかえします
    }

}