package com.example.demo.models;
import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
//お問い合わせ画面保存
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

//@Data
@Entity
@Table(name = "inquiry")
public class InquiryForm implements Serializable {
	private static final long serialVersionUID = -6647247658748349084L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
//EntityのIDは 必ず Long
	
	@NotBlank
	@Size(max = 10)
	private String name;

	@NotBlank
	@Email
	private String mail;

	@NotBlank
	@Size(max = 400)
	private String content;
	
	
	public Long getId() { //longにする
        return id;
    }

    public void setId(Long id) { //longにする
        this.id = id;
    }
    
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
    
 // --- toString メソッド ---
    @Override
    public String toString() {
        return "InquiryForm [id=" + id + ", name=" + name + ", mail=" + mail + ", content=" + content + "]";
    }

    // フォームをリセットするメソッド
    public void clear() {
    	
        this.name = null;
        this.mail = null;
        this.content = null;
    }

}
