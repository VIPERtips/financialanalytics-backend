package com.tadiwa.financialanalytics.model;

import java.time.LocalDate;
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
public class Transaction {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	private LocalDate date;
	@Column(columnDefinition = "TEXT")
	private String description;
	@Column(columnDefinition = "Decimal")
	private double amount;
	@Enumerated(EnumType.STRING)
	private Type type;
	@Enumerated(EnumType.STRING)
	private Category category;
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "user_id", referencedColumnName = "userId", nullable = false)
	private User user;
	
	public Transaction() {}
	
	public Transaction(LocalDate date, String description, double amount, Type type, Category category, User user) {
		this.date = date;
		this.description = description;
		this.amount = amount;
		this.type = type;
		this.category = category;
		this.user = user;
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public LocalDate getDate() {
		return date;
	}
	
	public void setDate(LocalDate date) {
		this.date = date;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public double getAmount() {
		return amount;
	}
	
	public void setAmount(double amount) {
		this.amount = amount;
	}
	
	public Type getType() {
		return type;
	}
	
	@JsonProperty("type")
	public void setType(Type type) {
		this.type = type;
	}
	
	public Category getCategory() {
		return category;
	}
	
	@JsonProperty("category")
	public void setCategory(Category category) {
		this.category = category;
	}
	
	public User getUser() {
		return user;
	}
	
	public void setUser(User user) {
		this.user = user;
	}
}
