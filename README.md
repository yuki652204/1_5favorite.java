# springboot-dev-docker-template
以下の記事のサンプルです。  
https://qiita.com/devnokiyo/items/214aa24d60764f0f55f6


# ECサイト向け商品管理システム (Admin Product Management)

Spring Boot と Docker を使用して開発した、商品一覧・管理用のバックエンドシステムです。
単なる CRUD 機能の実装に留まらず、データの整合性とユーザー体験（UX）を考慮したロジックを実装しています。

## 🚀 実装した主要機能
- **商品の一覧表示・編集・削除**: JPA を使用した効率的なデータベース操作。
- **お気に入り数カウント機能**: 各商品に対するユーザーのお気に入り登録数をリアルタイムで集計し一覧に表示。
- **データ不整合の防止（削除制限ロジック）**: 
  お気に入り登録されている商品をそのまま削除すると、ユーザー側のマイページ等でエラーが発生する可能性があるため、システム側で削除をブロックするバリデーションを実装。

## 💡 技術的なこだわり・工夫点

### 1. サービス層 (Service Layer) へのビジネスロジック分離
Controller が直接データベースを操作するのではなく、`ProductService` クラスを設けることで、削除可否の判定などの「ビジネスロジック」を分離しました。これにより、コードの再利用性とテストのしやすさを向上させています。

### 2. Spring Data JPA によるリレーション管理
`Product` エンティティと `Favorite` エンティティを適切に紐付け、お気に入り件数の取得をシンプルかつ高速に行えるよう設計しました。

### 3. フロントエンドと連携したエラーハンドリング
削除がブロックされた際、単にエラー画面を出すのではなく、`RedirectAttributes` を活用して Java から JavaScript へメッセージを渡し、ブラウザのアラート（ポップアップ）としてユーザーに通知する仕組みを構築しました。

## 🛠 使用技術
- **Language**: Java 11
- **Framework**: Spring Boot 2.x
- **ORM**: Spring Data JPA
- **Database**: MySQL 8.0 (Docker)
- **View**: Thymeleaf, JavaScript, CSS
- **Infrastructure**: Docker / Docker Compose

## 📦 開発環境のセットアップ
1. リポジトリをクローン
2. `docker` ディレクトリにて実行
   ```bash
   docker-compose up --build