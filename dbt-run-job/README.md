# dbt-run-job

dbtをCloud Run Jobで実行するためのコンテナイメージです。

## 構成

```
dbt-run-job/
├── Dockerfile          # コンテナイメージ定義
├── main.py             # エントリポイント（dbt runを実行）
├── pyproject.toml      # 依存関係（uv管理）
├── profiles/
│   └── profiles.yml    # BigQuery接続設定（環境変数参照）
└── dbt_run/            # dbtプロジェクト
```

## 環境変数

Cloud Run Job実行時に必要な環境変数です（Terraformで設定済み）。

| 変数名 | 説明 | 例 |
|---|---|---|
| `BIGQUERY_PROJECT` | BigQueryのGCPプロジェクトID | `self-finance-observatory` |
| `BIGQUERY_DATASET` | dbtのデフォルト出力先データセット | `cleaned` |
| `GCS_BUCKET_URL` | 外部テーブルが参照するGCSバケットURL | `gs://your-bucket-name` |

## イメージのビルドとプッシュ

Macのシリコン（arm64）でビルドする場合、`--platform linux/amd64` が必要です。

```bash
cd dbt-run-job

# ビルド
docker build --platform linux/amd64 \
  -t asia-northeast1-docker.pkg.dev/self-finance-observatory/dbt-run-job-repo/dbt-run-job:latest \
  .

# Artifact Registry に認証（初回のみ）
gcloud auth configure-docker asia-northeast1-docker.pkg.dev

# プッシュ
docker push asia-northeast1-docker.pkg.dev/self-finance-observatory/dbt-run-job-repo/dbt-run-job:latest
```

## Cloud Run Job の実行

```bash
# 実行
gcloud run jobs execute dbt-run-job --region asia-northeast1

# 完了まで待つ場合
gcloud run jobs execute dbt-run-job --region asia-northeast1 --wait
```

## ログの確認

```bash
gcloud logging read "resource.type=cloud_run_job AND resource.labels.job_name=dbt-run-job" \
  --limit 50 \
  --order desc \
  --format "value(textPayload)"
```
