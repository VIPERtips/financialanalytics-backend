package com.tadiwa.financialanalytics.model;

public class UserDto {
	private int id;
	private String username,email;
	private String password;
	private String confirmPassword;
	
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
	public String getConfirmPassword() {
		return confirmPassword;
	}
	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public UserDto(int id, String username, String email) {
		this.id = id;
		this.username = username;
		this.email = email;
		
	}
	
	
}
