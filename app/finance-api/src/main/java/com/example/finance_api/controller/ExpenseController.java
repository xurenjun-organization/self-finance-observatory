package com.example.finance_api.controller;

import com.example.finance_api.api.DefaultApi;
import com.example.finance_api.model.ExpenseListResponse;
import com.example.finance_api.model.ExpenseSummaryResponse;
import com.example.finance_api.service.ExpenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ExpenseController implements DefaultApi {

    private final ExpenseService expenseService;

    @Override
    public ResponseEntity<ExpenseListResponse> apiExpensesGet(Integer year, Integer month) {
        ExpenseListResponse response = expenseService.getExpenses(year, month);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<ExpenseSummaryResponse> apiExpensesSummaryGet(Integer year, Integer month) {
        ExpenseSummaryResponse response = expenseService.getSummary(year, month);
        return ResponseEntity.ok(response);
    }
}
