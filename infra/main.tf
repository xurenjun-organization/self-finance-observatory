# Terraformバックエンド用GCSバケット作成
module "backend" {
  source   = "./modules/backend"
  project  = var.project
  location = var.region
}

# GCS作成
module "gcs" {
  source = "./modules/gcs"

  project     = var.project
  region      = var.region
  bucket_name = var.bucket_name
}

# BigQuery作成
module "bq" {
  source = "./modules/bq"

  project     = var.project
  location    = var.region
  dataset_ids = ["raw", "cleaned", "mart"]
}

# サービスアカウント作成
module "service_account" {
  source = "./modules/service_account"

  account_id             = "dbt-run-job-sa"
  display_name           = "dbt Run Job Service Account"
  project                = var.project
  project_roles          = ["roles/bigquery.jobUser", "roles/artifactregistry.reader"]
  bigquery_dataset_ids   = module.bq.bq_dataset_ids
  bigquery_dataset_roles = ["roles/bigquery.dataEditor"]
  storage_bucket_name    = module.gcs.bucket_name
  storage_bucket_roles   = ["roles/storage.objectViewer"]
}

# Artifact Registry作成
module "artifact_registry" {
  source        = "./modules/artifact_registry"
  project       = var.project
  location      = var.region
  repository_id = "dbt-run-job-repo"
}

# Cloud Run Job作成
module "run_job" {
  source                = "./modules/run_job"
  project               = var.project
  job_name              = "dbt-run-job"
  location              = var.region
  run_job_image         = "${module.artifact_registry.repository_image_url}/dbt-run-job:latest"
  service_account_email = module.service_account.email
  gcs_bucket_url        = module.gcs.bucket_url
  bigquery_project      = var.project
  bigquery_dataset      = "cleaned"
}