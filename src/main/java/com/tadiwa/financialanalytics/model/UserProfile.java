package com.tadiwa.financialanalytics.model;

import jakarta.persistence.*;

@Entity
public class UserProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int profileId;
    
    private String name;
    
    private String currency;
    
    private double income;  
    
    private String theme;  
    
    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "userId", nullable = false)
    private User user;  
    
    public UserProfile() {}

    // Getters and Setters
    public int getProfileId() {
        return profileId;
    }

    public void setProfileId(int profileId) {
        this.profileId = profileId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public double getIncome() {
        return income;
    }

    public void setIncome(double income) {
        this.income = income;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}

