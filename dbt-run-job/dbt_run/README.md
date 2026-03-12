# dbt_run

self-finance-observatoryのdbtプロジェクトです。

## 環境変数

| 変数名 | 説明 | 例 |
|---|---|---|
| `GCS_BUCKET_URL` | 外部テーブルが参照するGCSバケットURL | `gs://your-bucket-name` |
| `BIGQUERY_PROJECT` | BigQueryのGCPプロジェクトID | `self-finance-observatory` |
| `BIGQUERY_DATASET` | dbtのデフォルト出力先データセット | `cleaned` |

## ローカル開発セットアップ

### 1. 依存パッケージのインストール

```bash
cd dbt-run-job
uv sync
```

### 2. `~/.dbt/profiles.yml` を作成

```yaml
dbt_run:
  target: dev
  outputs:
    dev:
      type: bigquery
      method: oauth
      project: self-finance-observatory
      dataset: cleaned
      location: asia-northeast1
      threads: 4
```

### 3. GCP認証

```bash
gcloud auth application-default login
```

### 4. `.env` ファイルを作成

```bash
cp .env.example .env
```

`.env` を編集してバケットURLを設定します。

```
GCS_BUCKET_URL=gs://your-bucket-name
```

### 5. dbt パッケージのインストール

```bash
(set -a && source .env && set +a && uv run dbt deps)
```

## 外部テーブルの作成・更新

外部テーブルの定義は `models/sources/` 以下のYAMLファイルで管理しています。
定義を変更した場合は以下のコマンドで反映します。

```bash
# 初回 or 強制再作成
(set -a && source .env && set +a && uv run dbt run-operation stage_external_sources --vars 'ext_full_refresh: true')

# 通常の更新
(set -a && source .env && set +a && uv run dbt run-operation stage_external_sources)
```

## 通常の実行

```bash
(set -a && source .env && set +a && uv run dbt run)
(set -a && source .env && set +a && uv run dbt test)
```

> `( )` で囲むことで、環境変数が現在のシェルに影響しません。
