package com.example.demo.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.example.demo.models.User;
import com.example.demo.services.UserService; // Serviceをインポート

@Controller
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    // コンストラクタでUserServiceを注入
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // マイページ表示
    @GetMapping
    public String myPage(Model model) {
        // Service経由で取得
        User user = userService.getUserById(1L);
        model.addAttribute("user", user);
        return "user/users";
    }

    // 編集画面
    @GetMapping("/edit")
    public String edit(@RequestParam Long id, Model model) {
        // Service経由で取得
        User user = userService.getUserById(id);
        model.addAttribute("user", user);
        return "user/usersnewedit";
    }

    // 更新処理
    @PostMapping("/update")
    public String update(@RequestParam Long id, @RequestParam String name) {
        // Serviceに「名前を更新して」と指示を出すだけ
        userService.updateName(id, name);
        return "redirect:/";
    }
}