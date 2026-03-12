# module: run_job

dbt実行用のCloud Run Jobを作成するモジュールです。

## 変数

| 変数名 | 説明 |
|---|---|
| `job_name` | Cloud Run Job名（デフォルト: `dbt-run-job`） |
| `project` | GCPプロジェクトID |
| `location` | リージョン |
| `run_job_image` | 使用するコンテナイメージのURL |
| `service_account_email` | アタッチするサービスアカウント |
| `gcs_bucket_url` | 外部テーブルが参照するGCSバケットURL |
| `bigquery_project` | dbtが接続するBigQueryプロジェクトID |
| `bigquery_dataset` | dbtのデフォルト出力先データセット |

## コンテナに渡す環境変数

| 変数名 | 値 |
|---|---|
| `GCS_BUCKET_URL` | `var.gcs_bucket_url` |
| `BIGQUERY_PROJECT` | `var.bigquery_project` |
| `BIGQUERY_DATASET` | `var.bigquery_dataset` |

## リソース設定

| 項目 | 値 |
|---|---|
| CPU | 1 |
| メモリ | 1Gi |
| `deletion_protection` | `false`（Terraformで削除・再作成可能） |
