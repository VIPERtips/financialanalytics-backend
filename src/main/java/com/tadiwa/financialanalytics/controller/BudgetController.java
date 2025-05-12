package com.tadiwa.financialanalytics.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tadiwa.financialanalytics.model.ApiResponse;
import com.tadiwa.financialanalytics.model.Budget;
import com.tadiwa.financialanalytics.model.User;
import com.tadiwa.financialanalytics.service.BudgetService;
import com.tadiwa.financialanalytics.service.JwtService;
import com.tadiwa.financialanalytics.service.UserService;


import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/budget")
public class BudgetController {
	
	@Autowired
	private BudgetService budgetService;
	
	@Autowired
	private JwtService jwtService;
	
	@Autowired
	private UserService userService;
	
	@PostMapping
	@PreAuthorize("hasRole('USER')")
	public ResponseEntity<ApiResponse<Budget>> createOrUpdateBudget(HttpServletRequest request, @RequestBody Budget budget){
		try {
			String token = extractTokenFromRequest(request);
			String username = jwtService.extractUsername(token);
			User user = userService.getUserByUsername(username);
			Budget createOrUpdateBudget = budgetService.createOrUpdateBudget(user.getUserId(), budget);
			return ResponseEntity.ok(new ApiResponse<>("Budget set successfully, you're all set",true,createOrUpdateBudget));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(new ApiResponse<>(e.getMessage(), false, null));
		}
	}
	
	@GetMapping("/my-budgets")
	@PreAuthorize("hasRole('USER')")
	public ResponseEntity<ApiResponse<Page<Budget>>> getTransactionByUser(
			HttpServletRequest request,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) {
		try {
			String token = extractTokenFromRequest(request);
			String username = jwtService.extractUsername(token);
			User user = userService.getUserByUsername(username);
			Page<Budget> budgets = budgetService.getBudgetsForUser(user, page, size);
			return ResponseEntity.ok(new ApiResponse<>("Here is your budget info, fam!", true, budgets));
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(new ApiResponse<>(e.getMessage(), false, null));
		}
	}
	
	@GetMapping("/{id}")
	@PreAuthorize("hasAnyRole('ADMIN', 'USER')")
	public ResponseEntity<ApiResponse<Budget>> getTransactionById(@PathVariable int id) {
		try {
			Budget budget = budgetService.getBudgetById(id);
			return ResponseEntity.ok(new ApiResponse<>("Here is the budget info bro for user", true, budget));
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(new ApiResponse<>(e.getMessage(), false, null));
		}
	}
	
	@PutMapping("/{id}")
	@PreAuthorize("hasAnyRole('ADMIN', 'USER')")
	public ResponseEntity<ApiResponse<Budget>> updateSpent(@PathVariable int id,@RequestParam double amount){
		try {
			budgetService.updateSpentAmount(id, amount);
			return ResponseEntity.ok(new ApiResponse<>("Spent successfully updated", true, null));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(new ApiResponse<>(e.getMessage(), false, null));
		}
	}
	
	@DeleteMapping("/{id}")
	@PreAuthorize("hasAnyRole('USER')")
	public ResponseEntity<ApiResponse<?>> deleteBudget(@PathVariable int id){
		try {
			budgetService.deleteBudget(id);
			return ResponseEntity.ok(new ApiResponse<>("Budget deleted successfully fam!", true, null));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(new ApiResponse<>(e.getMessage(), false, null));
		}
	}
	
	
	private String extractTokenFromRequest(HttpServletRequest request) {
		String token = request.getHeader("Authorization");
		if (token != null && token.startsWith("Bearer ")) {
			return token.substring(7);
		}
		return null;
	}
}
