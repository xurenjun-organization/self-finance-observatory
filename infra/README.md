# infra

Terraformでself-finance-observatoryのGCPリソースを管理します。

## 構成

| モジュール | 用途 |
|---|---|
| `backend` | TerraformのstateをGCSで管理するためのバケット |
| `gcs` | データ格納用GCSバケット |
| `bq` | BigQueryデータセット |
| `service_account` | Cloud Run Job用サービスアカウント |
| `artifact_registry` | Dockerイメージ管理リポジトリ |
| `run_job` | dbt実行用Cloud Run Job |

## 初回セットアップ（Bootstrap手順）

Terraformのstateを保存するGCSバケット自体をTerraformで管理するため、
初回のみ以下の手順が必要です。

```bash
cd infra

# Step1: ローカルstateでバックエンド用バケットだけ先に作成
terraform init
terraform apply -target=module.backend

# Step2: ローカルstateをGCSに移行（backend.tf が自動生成された後）
terraform init -migrate-state

# Step3: 残りの全リソースを適用
terraform apply
```

## 通常の使い方

初回セットアップ済みであれば、以下のコマンドで操作できます。

```bash
cd infra

terraform plan
terraform apply
```

## dbt-run-jobのデプロイ手順

インフラ適用後、以下の手順でイメージをビルド・プッシュしてCloud Run Jobを更新します。

```bash
# 1. イメージをビルド（linux/amd64 を指定）
cd dbt-run-job
docker build --platform linux/amd64 \
  -t asia-northeast1-docker.pkg.dev/self-finance-observatory/dbt-run-job-repo/dbt-run-job:latest \
  .

# 2. Artifact Registry に認証（初回のみ）
gcloud auth configure-docker asia-northeast1-docker.pkg.dev

# 3. プッシュ
docker push asia-northeast1-docker.pkg.dev/self-finance-observatory/dbt-run-job-repo/dbt-run-job:latest

# 4. Cloud Run Job を実行
gcloud run jobs execute dbt-run-job --region asia-northeast1 --wait
```

## CI（GitHub Actions）での使い方

`backend.tf` はセキュリティのため `.gitignore` に含まれており、CIには存在しません。
そのため `terraform init` 時に `-backend-config` フラグでバケット名を渡す必要があります。

バケット名はGitHub Secretsに登録してください。

| Secret名 | 値 |
|---|---|
| `TF_BACKEND_BUCKET` | Terraformバックエンド用GCSバケット名 |

GitHub Actionsでの設定例:

```yaml
- name: Terraform Init
  run: |
    terraform init \
      -backend-config="bucket=${{ secrets.TF_BACKEND_BUCKET }}"

- name: Terraform Plan
  run: terraform plan
```
