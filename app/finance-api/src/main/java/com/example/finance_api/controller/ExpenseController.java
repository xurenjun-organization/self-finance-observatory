package com.example.finance_api.controller;

import com.example.finance_api.api.DefaultApi;
import com.example.finance_api.model.ExpenseListResponse;
import com.example.finance_api.model.ExpenseSummaryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ExpenseController implements DefaultApi {

    @Override
    public ResponseEntity<ExpenseListResponse> apiExpensesGet(Integer year, Integer month) {
        // TODO: BigQueryからデータを取得する
        var response = new ExpenseListResponse()
                .year(year)
                .month(month)
                .total(0)
                .expenses(List.of());
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<ExpenseSummaryResponse> apiExpensesSummaryGet(Integer year, Integer month) {
        // TODO: BigQueryからデータを取得する
        var response = new ExpenseSummaryResponse()
                .year(year)
                .month(month)
                .total(0)
                .categories(List.of());
        return ResponseEntity.ok(response);
    }
}
