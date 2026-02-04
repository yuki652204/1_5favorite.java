package com.example.demo.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.demo.models.User;
// ここは複数形 forms パッケージの UserForm クラス
import com.example.demo.models.forms.UserForm;
import com.example.demo.services.UserService;

@Controller
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

 // ユーザー一覧表示（http://localhost:8080/user でアクセスした時）
    @GetMapping
    public String index(Model model) {
        // 1. 全件リストではなく、IDを指定して「1人分」だけ取得する
        User oneUser = userService.getUserById(1L); 
        
        // 2. HTML側の ${user.name} に合わせて "user" という名前で渡す
        model.addAttribute("user", oneUser); 
        
        return "user/users";
    }
    
    // 編集画面表示
 // 編集画面表示
    @GetMapping("/edit/{id}")
    public String editUser(@PathVariable("id") Long id, Model model) {
        // 1. サービスからユーザーの実体を取得
        User user = userService.getUserById(id);
        
        // 2. 画面表示用の Form オブジェクトを作成し、データを詰め替える
        UserForm form = new UserForm();
        form.setId(user.getId());
        form.setName(user.getName());

        // 3. "userForm" という名前で HTML に渡す
        model.addAttribute("userForm", form);
        
        return "user/usersnewedit";
    } 
    

    // 更新処理
    @PostMapping("/update")
    public String update(
            @Validated @ModelAttribute("userForm") UserForm form, // ここも UserForm に修正
            BindingResult result) {

        if (result.hasErrors()) {
            return "user/usersnewedit";
        }

        userService.updateName(form.getId(), form.getName());
        
        return "redirect:/user";
    }
}