variable "job_name" {
  description = "Cloud Run Jobの名前"
  type        = string
  default     = "dbt-run-job"
}

variable "project" {
  description = "GCPプロジェクトID"
  type        = string
}

variable "location" {
  description = "Cloud Run Jobのロケーション"
  type        = string
}

variable "service_account_email" {
  description = "Cloud Run Jobが使用するサービスアカウントのメールアドレス"
  type        = string
}

variable "run_job_image" {
  description = "Cloud Run Jobで使用するコンテナイメージのURL"
  type        = string
}

variable "gcs_bucket_url" {
  description = "dbtの外部テーブルが参照するGCSバケットURL（例: gs://bucket-name）"
  type        = string
}

variable "bigquery_project" {
  description = "dbtが接続するBigQueryのGCPプロジェクトID"
  type        = string
}

variable "bigquery_dataset" {
  description = "dbtのデフォルト出力先データセット"
  type        = string
}