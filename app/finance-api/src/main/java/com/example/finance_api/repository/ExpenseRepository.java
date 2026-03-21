package com.example.finance_api.repository;

import com.example.finance_api.model.Expense;
import com.example.finance_api.service.CategoryResolver;
import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.QueryJobConfiguration;
import com.google.cloud.bigquery.QueryParameterValue;
import com.google.cloud.bigquery.TableResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ExpenseRepository {

    private final BigQuery bigQuery;
    private final CategoryResolver categoryResolver;

    public List<Expense> findByYearAndMonth(Integer year, Integer month) {
        String query =
            "SELECT " +
            "  used_at, " +
            "  store_name, " +
            "  current_month_payment " +
            "FROM `self-finance-observatory.cleaned.rakuten_usage_details` " +
            "WHERE payment_month = DATE(@year, @month, 1) " +
            "  AND current_month_payment IS NOT NULL " +
            "ORDER BY used_at";

        QueryJobConfiguration queryConfig = QueryJobConfiguration.newBuilder(query)
                .addNamedParameter("year", QueryParameterValue.int64(year))
                .addNamedParameter("month", QueryParameterValue.int64(month))
                .build();

        try {
            TableResult results = bigQuery.query(queryConfig);
            List<Expense> expenses = new ArrayList<>();

            results.iterateAll().forEach(row -> {
                Expense expense = new Expense();
                expense.setId(UUID.randomUUID().toString());

                if (row.get("used_at") != null && !row.get("used_at").isNull()) {
                    String dateStr = row.get("used_at").getStringValue();
                    expense.setDate(LocalDate.parse(dateStr, DateTimeFormatter.ISO_DATE));
                }

                String storeName = null;
                if (row.get("store_name") != null && !row.get("store_name").isNull()) {
                    storeName = row.get("store_name").getStringValue();
                }

                if (row.get("current_month_payment") != null && !row.get("current_month_payment").isNull()) {
                    expense.setAmount((int) row.get("current_month_payment").getLongValue());
                }

                // カテゴリ分類と正規化名を設定
                CategoryResolver.ResolveResult resolved = categoryResolver.resolve(storeName);
                expense.setCategory(resolved.category());
                // マッチしたキーをdescriptionにセット（地名除去）、未分類の場合は元のstore_nameをそのまま使う
                expense.setDescription(resolved.normalizedName() != null ? resolved.normalizedName() : storeName);

                expenses.add(expense);
            });

            return expenses;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("BigQuery query interrupted", e);
        }
    }
}
