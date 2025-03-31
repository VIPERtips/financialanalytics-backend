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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.RequestBody;

import com.tadiwa.financialanalytics.model.ApiResponse;
import com.tadiwa.financialanalytics.model.Transaction;
import com.tadiwa.financialanalytics.model.User;
import com.tadiwa.financialanalytics.service.JwtService;
import com.tadiwa.financialanalytics.service.TransactionService;
import com.tadiwa.financialanalytics.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {
	
	@Autowired
	private TransactionService transactionService;
	
	@Autowired
	private JwtService jwtService;
	
	@Autowired
	private UserService userService;
	
	@GetMapping
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<ApiResponse<Page<Transaction>>> getAllTransactions(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) {
		try {
			Page<Transaction> transactionsPage = transactionService.getAllTransactions(page, size);
			return ResponseEntity.ok(new ApiResponse<>("Transactions retrieved successfully", true, transactionsPage));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT)
					.body(new ApiResponse<>("No transactions found", false, null));
		}
	}
	
	@PostMapping
	@PreAuthorize("hasRole('USER')")
	public ResponseEntity<ApiResponse<Transaction>> createTransaction(HttpServletRequest request,
			@RequestBody Transaction transaction) {
		try {
			String token = extractTokenFromRequest(request);
			String username = jwtService.extractUsername(token);
			User user = userService.getUserByUsername(username);
			Transaction createdTransaction = transactionService.createTransaction(transaction, user.getUserId());
			return ResponseEntity.ok(new ApiResponse<>("Transaction created successfully", true, createdTransaction));
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(new ApiResponse<>(e.getMessage(), false, null));
		}
	}
	@PostMapping("/upload")
	@PreAuthorize("hasRole('USER')")
	public ResponseEntity<ApiResponse<Map<String, Object>>> uploadTransactions(
	        @RequestParam("file") MultipartFile file,
	        @RequestParam(value = "validateOnly", required = false, defaultValue = "false") boolean validateOnly,
	        HttpServletRequest request
	) {
	    try {
	        String token = extractTokenFromRequest(request);
	        String username = jwtService.extractUsername(token);
	        User user = userService.getUserByUsername(username);

	        List<Transaction> transactions = transactionService.processTransactionCsv(file, user.getUserId(), validateOnly);

	        if (validateOnly) {
	            return ResponseEntity.ok(new ApiResponse<>(
	                    "CSV validated successfully",
	                    true,
	                    Map.of("preview", transactions, "count", transactions.size())
	            ));
	        } else {
	            return ResponseEntity.ok(new ApiResponse<>(
	                    "Transactions uploaded and saved successfully",
	                    true,
	                    Map.of("savedCount", transactions.size())
	            ));
	        }
	    } catch (RuntimeException e) {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	                .body(new ApiResponse<>(e.getMessage(), false, null));
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body(new ApiResponse<>("Failed to process CSV: " + e.getMessage(), false, null));
	    }
	}

	
	@PutMapping("/{id}")
	@PreAuthorize("hasAnyRole('ADMIN', 'USER')")
	public ResponseEntity<ApiResponse<Transaction>> updateTransaction(@PathVariable int id, @RequestBody Transaction transaction, HttpServletRequest request) {
		try {
			String token = extractTokenFromRequest(request);
			String username = jwtService.extractUsername(token);
			User user = userService.getUserByUsername(username);
			Transaction existingTransaction = transactionService.getTransactionById(id);
			if (existingTransaction.getUser().getUserId() != user.getUserId()) {
				throw new RuntimeException("You can only update the Transaction you own.");
			}
			Transaction updatedTransaction = transactionService.updateTransaction(transaction, id);
			return ResponseEntity.ok(new ApiResponse<>("Transaction updated successfully", true, updatedTransaction));
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(new ApiResponse<>(e.getMessage(), false, null));
		}
	}
	
	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<ApiResponse<String>> deleteTransaction(@PathVariable int id) {
		try {
			transactionService.deleteTransaction(id);
			return ResponseEntity.ok(new ApiResponse<>("Transaction deleted successfully.", true, null));
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(new ApiResponse<>("Transaction not found with ID: " + id, false, null));
		}
	}
	
	@GetMapping("/my-transactions")
	@PreAuthorize("hasRole('USER')")
	public ResponseEntity<ApiResponse<Page<Transaction>>> getTransactionByUser(
			HttpServletRequest request,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) {
		try {
			String token = extractTokenFromRequest(request);
			String username = jwtService.extractUsername(token);
			User user = userService.getUserByUsername(username);
			Page<Transaction> transactions = transactionService.getTransactionsForUser(user, page, size);
			return ResponseEntity.ok(new ApiResponse<>("Transactions retrieved successfully", true, transactions));
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(new ApiResponse<>(e.getMessage(), false, null));
		}
	}


	
	@GetMapping("/{id}")
	@PreAuthorize("hasAnyRole('ADMIN', 'USER')")
	public ResponseEntity<ApiResponse<Transaction>> getTransactionById(@PathVariable int id) {
		try {
			Transaction transaction = transactionService.getTransactionById(id);
			return ResponseEntity.ok(new ApiResponse<>("Transaction retrieved successfully", true, transaction));
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(new ApiResponse<>("Transaction not found with ID: " + id, false, null));
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
