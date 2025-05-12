package com.tadiwa.financialanalytics.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
@Entity
public class Budget {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	@Enumerated(EnumType.STRING)
	@JsonProperty("category")
	@Column(unique = true)
	private Category category;
	@JsonProperty("budgetLimit")
	private double budgetLimit;
	@JsonProperty("spent")
	private double spent = 0;
	
	@Enumerated(EnumType.STRING)
	private Period period;
	
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "user_id", referencedColumnName = "userId", nullable = false)
	private User user;
	public Budget() {
		// TODO Auto-generated constructor stub
	}
	public Category getCategory() {
		return category;
	}
	public void setCategory(Category category) {
		this.category = category;
	}
	public double getbudgetLimit() {
		return budgetLimit;
	}
	public void setbudgetLimit(double budgetLimit) {
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
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public Budget(Category category, double budgetLimit, double spent, Period period, User user) {
		this.category = category;
		this.budgetLimit = budgetLimit;
		this.spent = spent;
		this.period = period;
		this.user = user;
	}
	
	
}
