package com.example.demo.services;

// 各種アノテーションや依存クラスをインポート
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.models.entity.Inquiry;
import com.example.demo.models.forms.InquiryForm;
import com.example.demo.repositories.InquiryRepository;

/**
 * お問い合わせに関するビジネスロジックを管理するサービスクラス
 */
@Service // Springコンテナにこのクラスを「サービス」として登録する
@Transactional // このクラスのメソッド実行時にDBトランザクションを自動管理する
public class InquiryService {

    // DB操作のためのRepositoryを保持する変数（不変にするためfinalを指定）
    private final InquiryRepository inquiryRepository;

    /**
     * コンストラクタ。SpringからInquiryRepositoryを注入（Dependency Injection）する
     */
    public InquiryService(InquiryRepository inquiryRepository) {
        // 受け取ったRepositoryを変数に代入して初期化する
        this.inquiryRepository = inquiryRepository;
    }

    /**
     * お問い合わせフォームからのデータをエンティティに変換して保存する
     */
    public void saveInquiry(InquiryForm form) {

        // DB保存用のエンティティ（Inquiry）を作成
        Inquiry inquiry = new Inquiry();
        
        // フォームから送られた「名前」をエンティティにセット
        inquiry.setName(form.getName());
        // フォームから送られた「メールアドレス」をエンティティにセット
        inquiry.setMail(form.getMail());
        // フォームから送られた「内容」をエンティティにセット
        inquiry.setContent(form.getContent());

        // セットされた情報をリポジトリ経由でデータベースに保存（INSERT/UPDATE）する
        inquiryRepository.save(inquiry);
    }
    
    /**
     * 指定されたIDに基づいてお問い合わせ情報を削除する
     */
    public void delete(Long id) {
        // Repositoryの標準メソッド deleteById を使って、指定IDのデータをDBから削除する
        inquiryRepository.deleteById(id);
    }
    
}