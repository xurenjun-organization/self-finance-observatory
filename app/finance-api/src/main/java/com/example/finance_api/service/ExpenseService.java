package com.example.finance_api.service;

import com.example.finance_api.model.Expense;
import com.example.finance_api.model.ExpenseListResponse;
import com.example.finance_api.model.ExpenseSummaryResponse;
import com.example.finance_api.model.CategorySummary;
import com.example.finance_api.repository.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExpenseService {

    private final ExpenseRepository expenseRepository;

    @Cacheable(value = "expenses", key = "{#year, #month}")
    public ExpenseListResponse getExpenses(Integer year, Integer month) {
        List<Expense> expenses = expenseRepository.findByYearAndMonth(year, month);

        int total = expenses.stream()
                .mapToInt(Expense::getAmount)
                .sum();

        return new ExpenseListResponse()
                .year(year)
                .month(month)
                .total(total)
                .expenses(expenses);
    }

    @Cacheable(value = "expenseSummary", key = "{#year, #month}")
    public ExpenseSummaryResponse getSummary(Integer year, Integer month) {
        List<Expense> expenses = expenseRepository.findByYearAndMonth(year, month);

        int total = expenses.stream()
                .mapToInt(Expense::getAmount)
                .sum();

        // カテゴリ別集計（現在は「未分類」固定のため1件のみ）
        CategorySummary categorySummary = new CategorySummary()
                .category("未分類")
                .amount(total)
                .percentage(total > 0 ? 100.0f : 0.0f);

        return new ExpenseSummaryResponse()
                .year(year)
                .month(month)
                .total(total)
                .categories(List.of(categorySummary));
    }
}
