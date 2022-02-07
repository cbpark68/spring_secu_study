package com.spring.secu03.user;

public class UserVO {
	private String username;
	private String password;
	private int enabled;
	
	public UserVO() {
		this("user", "1111", 1);
	}
	
	public UserVO(String username, String password) {
		this(username, password, 1);
	}
	
	public UserVO(String username, String password, int enabled) {
		super();
		this.username = username;
		this.password = password;
		this.enabled = enabled;
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
	public int getEnabled() {
		return enabled;
	}
	public void setEnabled(int enabled) {
		this.enabled = enabled;
	}
	
	
	
	
	

}
