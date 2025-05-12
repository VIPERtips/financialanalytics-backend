package com.tadiwa.financialanalytics.service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.tadiwa.financialanalytics.model.Transaction;
import com.tadiwa.financialanalytics.model.Type;
import com.tadiwa.financialanalytics.model.User;
import com.tadiwa.financialanalytics.repository.TransactionRepository;

@Service
public class ReportService {

	@Autowired
	private TransactionRepository transactionRepository;

	public Map<String, Object> generateProfitAndLossReport(LocalDate from, LocalDate to, User user) {
		List<Transaction> transactions = transactionRepository.findByUser_UserIdAndDateBetween(user.getUserId() , from, to);
		double totalIncome = 0.0;
		double totalExpense = 0.0;
		for (Transaction t : transactions) {
			if (t.getType() == Type.INCOME) {
				totalIncome += t.getAmount();
			} else if (t.getType() == Type.EXPENSE) {
				totalExpense += t.getAmount();
			}
		}
		double netProfit = totalIncome - totalExpense;
		List<Map<String, Object>> details = new ArrayList<>();
		for (LocalDate date = from.withDayOfMonth(1); !date.isAfter(to); date = date.plusMonths(1)) {
			double monthlyIncome = 0.0;
			double monthlyExpense = 0.0;
			for (Transaction t : transactions) {
				if (t.getDate().getMonth() == date.getMonth() && t.getDate().getYear() == date.getYear()) {
					if (t.getType() == Type.INCOME) {
						monthlyIncome += t.getAmount();
					} else if (t.getType() == Type.EXPENSE) {
						monthlyExpense += t.getAmount();
					}
				}
			}
			Map<String, Object> monthlyData = new HashMap<>();
			String monthName = date.getMonth().name().substring(0, 1)
					+ date.getMonth().name().substring(1).toLowerCase();
			monthlyData.put("month", monthName);
			monthlyData.put("income", monthlyIncome);
			monthlyData.put("expenses", monthlyExpense);
			monthlyData.put("profit", monthlyIncome - monthlyExpense);
			details.add(monthlyData);
		}
		Map<String, Object> response = new HashMap<>();
		Map<String, Double> summary = new HashMap<>();
		summary.put("totalIncome", totalIncome);
		summary.put("totalExpense", totalExpense);
		summary.put("netProfit", netProfit);
		response.put("summary", summary);
		response.put("details", details);
		return response;
	}

	public Map<String, Object> generateValidatedProfitAndLossReport(LocalDate from, LocalDate to, double salary, User user) {
		Map<String, Object> pnlReport = generateProfitAndLossReport(from, to, user);
		@SuppressWarnings("unchecked")
		Map<String, Double> summary = (Map<String, Double>) pnlReport.get("summary");
		double totalIncome = summary.get("totalIncome");
		if (Double.compare(totalIncome, salary) != 0) {
			pnlReport.put("validation", "Hey, Bro, your P&L total income (" + totalIncome
					+ ") doesn't match your salary (" + salary + "). Time to check your transactions!");
		} else {
			pnlReport.put("validation", "Look at you, matching numbers. Your P&L and salary are in sync.");
		}
		return pnlReport;
	}

	public Map<String, Object> generateBalanceSheetReport(LocalDate from, LocalDate to, User user) {
		List<Transaction> transactions = transactionRepository.findByUser_UserIdAndDateBetween(user.getUserId(), from, to);
		List<Map<String, Object>> assetsList = new ArrayList<>();
		List<Map<String, Object>> liabilitiesList = new ArrayList<>();
		double totalAssets = 0.0;
		double totalLiabilities = 0.0;
		for (Transaction t : transactions) {
			Map<String, Object> item = new HashMap<>();
			item.put("name", t.getDescription());
			item.put("value", t.getAmount());
			if (t.getType() == Type.INCOME) {
				assetsList.add(item);
				totalAssets += t.getAmount();
			} else if (t.getType() == Type.EXPENSE) {
				liabilitiesList.add(item);
				totalLiabilities += t.getAmount();
			}
		}
		double netWorth = totalAssets - totalLiabilities;
		Map<String, Object> summary = new HashMap<>();
		summary.put("totalAssets", totalAssets);
		summary.put("totalLiabilities", totalLiabilities);
		summary.put("netWorth", netWorth);
		Map<String, Object> response = new HashMap<>();
		response.put("assets", assetsList);
		response.put("liabilities", liabilitiesList);
		response.put("summary", summary);
		return response;
	}

	public Map<String, Object> generateTrendsReport(LocalDate from, LocalDate to, User user) {
		List<Transaction> transactions = transactionRepository.findByUser_UserIdAndDateBetween(user.getUserId(), from, to);
		Map<YearMonth, List<Transaction>> grouped = transactions.stream()
				.collect(Collectors.groupingBy(t -> YearMonth.from(t.getDate())));
		List<Map<String, Object>> trends = new ArrayList<>();
		YearMonth start = YearMonth.from(from);
		YearMonth end = YearMonth.from(to);
		while (!start.isAfter(end)) {
			List<Transaction> monthlyTxns = grouped.getOrDefault(start, Collections.emptyList());
			double income = monthlyTxns.stream().filter(t -> t.getType() == Type.INCOME)
					.mapToDouble(Transaction::getAmount).sum();
			double expenses = monthlyTxns.stream().filter(t -> t.getType() == Type.EXPENSE)
					.mapToDouble(Transaction::getAmount).sum();
			double savings = income - expenses;
			Map<String, Object> monthData = new HashMap<>();
			monthData.put("date", start.atEndOfMonth().toString());
			monthData.put("income", income);
			monthData.put("expenses", expenses);
			monthData.put("savings", savings);
			trends.add(monthData);
			start = start.plusMonths(1);
		}
		Map<String, Object> response = new HashMap<>();
		response.put("trends", trends);
		return response;
	}

	public Map<String, Object> generateSummaryReport(LocalDate from, LocalDate to, User user) {
		List<Transaction> transactions = transactionRepository.findByUser_UserIdAndDateBetween(user.getUserId(), from, to);
		double totalIncome = 0.0;
		double totalExpense = 0.0;
		for (Transaction t : transactions) {
			if (t.getType() == Type.INCOME) {
				totalIncome += t.getAmount();
			} else if (t.getType() == Type.EXPENSE) {
				totalExpense += t.getAmount();
			}
		}
		double netProfit = totalIncome - totalExpense;
		Map<String, Object> summary = new HashMap<>();
		summary.put("totalIncome", totalIncome);
		summary.put("totalExpense", totalExpense);
		summary.put("netProfit", netProfit);
		return summary;
	}
}
