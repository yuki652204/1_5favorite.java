package com.example.demo.models.forms;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class UserForm {

    private Long id;

    @NotBlank(message = "名前は必須入力です")
    @Size(max = 50, message = "名前は50文字以内で入力してください")
    private String name;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}