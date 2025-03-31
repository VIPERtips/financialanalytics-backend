package com.tadiwa.financialanalytics.model;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotNull;

@Entity
public class User implements UserDetails {
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int userId;
	@NotNull(message = "Please enter a username")
	private String username;
	@NotNull(message = "Please enter a password")
	private String password;
	@Column(unique = true)
	private String email;
	private String role = "USER";
	@OneToMany(mappedBy = "user", cascade = jakarta.persistence.CascadeType.ALL, orphanRemoval = true)
	private List<Transaction> transactions;
	private String resetToken;
	private LocalDateTime tokenExpiration;
	
	public User() {}
	
	public User(@NotNull(message = "Please enter a username") String username,
			@NotNull(message = "Please enter a password") String password, String role) {
		this.username = username;
		this.password = password;
		this.role = role;
	}
	
	public int getUserId() {
		return userId;
	}
	
	public void setUserId(int userId) {
		this.userId = userId;
	}
	
	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getRole() {
		return role;
	}
	
	public void setRole(String role) {
		this.role = role;
	}
	
	public List<Transaction> getTransactions() {
		return transactions;
	}
	
	public void setTransactions(List<Transaction> transactions) {
		this.transactions = transactions;
	}
	
	public String getResetToken() {
		return resetToken;
	}
	
	public void setResetToken(String resetToken) {
		this.resetToken = resetToken;
	}
	
	public LocalDateTime getTokenExpiration() {
		return tokenExpiration;
	}
	
	public void setTokenExpiration(LocalDateTime tokenExpiration) {
		this.tokenExpiration = tokenExpiration;
	}
	
	public boolean isResetTokenValid() {
		return tokenExpiration != null && LocalDateTime.now().isBefore(tokenExpiration);
	}
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return List.of(new SimpleGrantedAuthority("ROLE_" + role));
	}
}
