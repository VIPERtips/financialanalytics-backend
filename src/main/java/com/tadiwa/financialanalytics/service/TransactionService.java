package com.tadiwa.financialanalytics.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.tadiwa.financialanalytics.model.Category;
import com.tadiwa.financialanalytics.model.Transaction;
import com.tadiwa.financialanalytics.model.Type;
import com.tadiwa.financialanalytics.model.User;
import com.tadiwa.financialanalytics.repository.TransactionRepository;
import com.tadiwa.financialanalytics.repository.UserRepository;

import io.jsonwebtoken.io.IOException;

@Service
public class TransactionService {
	
	@Autowired
	private TransactionRepository transactionRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private UserService userService;
	
	
	public Transaction createTransaction(Transaction transaction, int id) {
		if (!categoryExists(transaction.getCategory().toString())) {
	        throw new RuntimeException("Invalid category: " + transaction.getCategory() + 
	            ". Allowed values: " + Arrays.toString(Category.values()));
	    }
	    if (!typeExists(transaction.getType().toString())) {
	        throw new RuntimeException("Invalid type: " + transaction.getType() + 
	            ". Allowed values: " + Arrays.toString(Type.values()));
	    }
		User user = userRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("User with ID " + id + " not found."));
		transaction.setUser(user);
		return transactionRepository.save(transaction);
	}
	
	public Transaction updateTransaction(Transaction req, int id) {
		Transaction existingTransaction = transactionRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("No Transaction found with the id: " + id));
		if (!categoryExists(req.getCategory().toString())) {
	        throw new RuntimeException("Invalid category: " + req.getCategory() + 
	            ". Allowed values: " + Arrays.toString(Category.values()));
	    }
	    if (!typeExists(req.getType().toString())) {
	        throw new RuntimeException("Invalid type: " + req.getType() + 
	            ". Allowed values: " + Arrays.toString(Type.values()));
	    }
		existingTransaction.setDate(req.getDate());
		existingTransaction.setDescription(req.getDescription());
		existingTransaction.setAmount(req.getAmount());
		existingTransaction.setCategory(req.getCategory());
		existingTransaction.setType(req.getType());
		return transactionRepository.save(existingTransaction);
	}
	
	public Transaction getTransactionById(int id) {
		return transactionRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Transaction not found with id: " + id));
	}
	
	public void deleteTransaction(int id) {
		Transaction transaction = getTransactionById(id);
		transactionRepository.delete(transaction);
	}
	
	public Page<Transaction> getTransactionsForUser(User user, int page, int size) {
		Pageable pageable = PageRequest.of(page, size, Sort.by("date").descending());
		Page<Transaction> transactionsPage = transactionRepository.findByUser(user, pageable);

		if (transactionsPage.isEmpty()) {
			throw new RuntimeException("Fam...you haven't made any transactions yet. Go spend or earn something.");
		}
		return transactionsPage;
	}



	
	public Page<Transaction> getAllTransactions(int page, int size) {
		Sort sort = Sort.by(Sort.Direction.DESC, "id");
		Pageable pageable = PageRequest.of(page, size, sort);
		Page<Transaction> transactionsPage = transactionRepository.findAll(pageable);
		if (transactionsPage.isEmpty()) {
			throw new RuntimeException("No Transactions found");
		}
		return transactionsPage;
	}
	
	public boolean categoryExists(String category) {
	    for (Category c : Category.values()) {
	        if (c.name().equalsIgnoreCase(category)) {
	            return true;
	        }
	    }
	    return false;
	}

	public boolean typeExists(String type) {
	    for (Type t : Type.values()) {
	        if (t.name().equalsIgnoreCase(type)) {
	            return true;
	        }
	    }
	    return false;
	}
	public List<Transaction> processTransactionCsv(MultipartFile file, int userId, boolean validateOnly) throws IOException, java.io.IOException {
	    List<Transaction> transactionList = new ArrayList<>();
	    User user = userService.getUser(userId);

	    try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
	        String line;
	        boolean skipHeader = true;

	        while ((line = reader.readLine()) != null) {
	            if (skipHeader) {
	                skipHeader = false;
	                continue;
	            }
	            String[] parts = line.split(",");

	            Transaction transaction = new Transaction();
	            transaction.setDate(LocalDate.parse(parts[0].trim()));
	            transaction.setAmount(Double.parseDouble(parts[1].trim()));
	            transaction.setDescription(parts[2].trim());
	            transaction.setType(Type.valueOf(parts[3].trim().toUpperCase()));
	            transaction.setCategory(Category.valueOf(parts[4].trim().toLowerCase()));
	            transaction.setUser(user);

	            if (!validateOnly) {
	                transactionRepository.save(transaction);
	            }

	            transactionList.add(transaction);
	        }
	    }
	    return transactionList;
	}

}
