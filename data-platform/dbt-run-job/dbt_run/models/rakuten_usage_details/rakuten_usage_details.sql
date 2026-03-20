{{ config(
  materialized='incremental',
  schema='cleaned',
  partition_by={
    "field": "payment_month",
    "data_type": "date",
    "granularity": "month"
  },
  incremental_strategy='insert_overwrite'
) }}

SELECT
  PARSE_DATE('%Y%m', REGEXP_EXTRACT(_FILE_NAME, r'enavi(\d{6})')) AS payment_month,
  PARSE_DATE('%Y/%m/%d', used_at) AS used_at,
  store_name,
  card_holder,
  payment_method,
  SAFE_CAST(NULLIF(amount, '') AS INT64) AS amount,
  SAFE_CAST(NULLIF(fee, '') AS INT64) AS fee,
  SAFE_CAST(NULLIF(total_amount, '') AS INT64) AS total_amount,
  SAFE_CAST(NULLIF(current_month_payment, '') AS INT64) AS current_month_payment
FROM {{ source('gcs_raw_rakuten', 'gcs_raw_rakuten') }}
WHERE used_at IS NOT NULL
  AND used_at != ''
{% if is_incremental() %}
  AND PARSE_DATE('%Y%m', REGEXP_EXTRACT(_FILE_NAME, r'enavi(\d{6})'))
      NOT IN (SELECT DISTINCT payment_month FROM {{ this }})
{% endif %}
