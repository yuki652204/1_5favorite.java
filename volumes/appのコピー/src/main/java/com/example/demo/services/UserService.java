package com.example.demo.services; // このクラスが「サービス層」に属することを宣言

import org.springframework.stereotype.Service; // Springにこのクラスを管理対象（Bean）として認識させるためのインポート
import org.springframework.transaction.annotation.Transactional; // データベースの「トランザクション（ひとまとまりの処理）」を管理するためのインポート
import com.example.demo.models.User; // ユーザー情報（Entity）を扱うためのインポート
import com.example.demo.repositories.UserRepository; // データベース操作を行うためのインポート

@Service // Spring Bootに対し、このクラスが「業務ロジックを書く場所（Service）」であることを教える。これによりControllerに注入（DI）可能になる
@Transactional // このクラスの全メソッドをトランザクションの対象にする。処理が成功すれば確定（Commit）、失敗（例外）すれば元に戻す（Rollback）
public class UserService {

    // 依存するリポジトリを定義（書き換え不可の final にするのがプロの定石）
    private final UserRepository userRepository;

    // コンストラクタ。Springが自動的に UserRepository の実体を探してここに渡してくれる（コンストラクタ注入）
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * IDを指定してユーザーを1件取得するメソッド
     */
    @Transactional(readOnly = true) // 読み取り専用に設定することで、データベースの処理負荷を軽くし、パフォーマンスを最適化する
    public User getUserById(Long id) {
        // IDで検索し、データがあれば返す。なければ「見つかりません」というエラー（例外）を投げる
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ユーザーが見つかりません。ID: " + id));
    }

    /**
     * ユーザーの名前を更新するメソッド
     */
    public void updateName(Long id, String newName) {
        // 1. 上で定義した getUserById を使い、DBから最新のユーザー情報を取得する
        User user = getUserById(id);
        
        // 2. ビジネスルールの適用（バリデーション）
        // 名前が null（存在しない）か、空白文字のみ（"  " など）の場合はエラーにする
        if (newName == null || newName.trim().isEmpty()) {
            throw new IllegalArgumentException("名前を空にすることはできません。");
        }
        
        // 3. 取得したユーザーオブジェクトの名前を書き換える
        user.setName(newName);
        
        // 4. データベースに保存。
        // @Transactional がついているため、実はこの行を書かなくてもメソッド終了時に自動でDBに反映（Dirty Checking）されるが、明示的に書くことが多い
        userRepository.save(user);
    }
}