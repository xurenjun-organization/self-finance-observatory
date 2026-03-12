resource "google_cloud_run_v2_job" "this" {
  name                = var.job_name
  location            = var.location
  deletion_protection = false

  template {
    template {
      containers {
        image = var.run_job_image
        env {
          name  = "GCS_BUCKET_URL"
          value = var.gcs_bucket_url
        }
        env {
          name  = "BIGQUERY_PROJECT"
          value = var.bigquery_project
        }
        env {
          name  = "BIGQUERY_DATASET"
          value = var.bigquery_dataset
        }
        resources {
          limits = {
            cpu    = "1"
            memory = "1Gi"
          }
        }
      }
      service_account = var.service_account_email
    }
  }
}