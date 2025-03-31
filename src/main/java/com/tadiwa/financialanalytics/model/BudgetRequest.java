package com.tadiwa.financialanalytics.model;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

public class BudgetRequest {
	private Category category;
	
	private double budgetLimit;
	
	private double spent = 0;
	
	
	public User getUser() {
		return user;
	}


	public void setUser(User user) {
		this.user = user;
	}


	private Period period;
	
	private User user;


	public Category getCategory() {
		return category;
	}


	public void setCategory(Category category) {
		this.category = category;
	}


	public double getBudgetLimit() {
		return budgetLimit;
	}


	public void setBudgetLimit(double budgetLimit) {
		this.budgetLimit = budgetLimit;
	}


	public double getSpent() {
		return spent;
	}


	public void setSpent(double spent) {
		this.spent = spent;
	}


	public Period getPeriod() {
		return period;
	}


	public void setPeriod(Period period) {
		this.period = period;
	}
	
	
}
