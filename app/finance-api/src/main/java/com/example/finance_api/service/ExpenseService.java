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
import java.util.Map;
import java.util.stream.Collectors;

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

        Map<String, Integer> byCategory = expenses.stream()
                .collect(Collectors.groupingBy(
                        Expense::getCategory,
                        Collectors.summingInt(Expense::getAmount)
                ));

        List<CategorySummary> categories = byCategory.entrySet().stream()
                .map(e -> new CategorySummary()
                        .category(e.getKey())
                        .amount(e.getValue())
                        .percentage(total > 0 ? Math.round(e.getValue() * 1000.0f / total) / 10.0f : 0.0f))
                .toList();

        return new ExpenseSummaryResponse()
                .year(year)
                .month(month)
                .total(total)
                .categories(categories);
    }
}
