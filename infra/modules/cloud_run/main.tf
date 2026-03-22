resource "google_cloud_run_service" "this" {
  name     = var.cloud_run_name
  location = var.location
  project  = var.project

  template {
    metadata {
      annotations = {
        "autoscaling.knative.dev/minScale" : "0"
        "autoscaling.knative.dev/maxScale" : "1"
      }
    }
    spec {
      containers {
        image = var.cloud_run_image
        env {
          name  = "BIGQUERY_PROJECT_ID"
          value = var.bigquery_project
        }
        env {
          name  = "API_KEY"
          value = var.auth_api_key
        }
        resources {
          limits = {
            "cpu"    = "1"
            "memory" = "2Gi"
          }
        }
      }
      service_account_name = var.service_account_email
    }
  }
}

resource "google_cloud_run_service_iam_member" "public" {
  service  = google_cloud_run_service.this.name
  location = google_cloud_run_service.this.location
  project  = google_cloud_run_service.this.project
  role     = "roles/run.invoker"
  member   = "allUsers"
}