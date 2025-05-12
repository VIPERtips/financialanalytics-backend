package com.tadiwa.financialanalytics.service;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.tadiwa.financialanalytics.model.Budget;
import com.tadiwa.financialanalytics.model.Category;
import com.tadiwa.financialanalytics.model.Transaction;
import com.tadiwa.financialanalytics.model.Type;
import com.tadiwa.financialanalytics.model.User;
import com.tadiwa.financialanalytics.repository.BudgetRepository;
import com.tadiwa.financialanalytics.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class BudgetService {
	@Autowired
	private BudgetRepository budgetRepository;
	
	@Autowired
	private UserRepository userRepository;
	

	public Budget createOrUpdateBudget(int id, Budget budget) {
	    System.out.println("trying to save the budget " + budget + " budget is " + budget.getCategory());

	    if (budget.getCategory() == null) {
	        throw new RuntimeException("Category not mentioned");
	    }

	    User user = userRepository.findById(id)
	            .orElseThrow(() -> new RuntimeException("User not found with ID: " + id));

	    Budget existingBudget = budgetRepository.findByUserAndCategory(user, budget.getCategory());

	    if (existingBudget != null) {
	        // Only block if it's not the same budget being updated
	        if (budget.getId() == 0 || existingBudget.getId() != budget.getId()) {
	            throw new RuntimeException("Fam... You already have a budget for category: " + budget.getCategory());
	        }
	    }

	    budget.setUser(user);
	    return budgetRepository.save(budget);
	}

	
	public Budget getBudgetById(int budgetID) {
		return budgetRepository.findById(budgetID)
				.orElseThrow(()-> new RuntimeException("Budget not found with the iD: "+budgetID));
	}
	
	public Page<Budget> getBudgetsForUser(User user, int page, int size) {
		Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
		Page<Budget> budgetPage = budgetRepository.findByUser(user, pageable);

		if (budgetPage.isEmpty()) {
			throw new RuntimeException("Fam...you haven't created any budgets yet.");
		}
		return budgetPage;
	}
	
	public void deleteBudget(int id) {
		Budget  budget = getBudgetById(id);
		budgetRepository.delete(budget);
	}
	
	public void updateSpentAmount(int budgetID,Double amount) {
		Budget budget = getBudgetById(budgetID);
		double newlimit = amount;
		budget.setbudgetLimit(newlimit);
		budget.setSpent(budget.getSpent());
		budgetRepository.save(budget);
	}
	
	public boolean categoryExists(String category) {
	    for (Category c : Category.values()) {
	        if (c.name().equalsIgnoreCase(category)) {
	            return true;
	        }
	    }
	    return false;
	}
	
	@Transactional
	public void updateBudgetSpentOnTransaction(User user, Transaction transaction) {
	    Budget budget = budgetRepository.findByUserAndCategory(user, transaction.getCategory());

	    if (budget != null) {
	        double newSpent = budget.getSpent() + transaction.getAmount();
	        budget.setSpent(newSpent);
	        budgetRepository.save(budget);
	    } else {
	        System.out.println("No budget found for category: " + transaction.getCategory());
	    }
	}


	public boolean periodExists(String type) {
	    for (Type t : Type.values()) {
	        if (t.name().equalsIgnoreCase(type)) {
	            return true;
	        }
	    }
	    return false;
	}
}
