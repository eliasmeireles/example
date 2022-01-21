package com.udemy.course.models;

import java.util.UUID;

public class User {

	private String userId;
	private String name;
	private String email;

	public User() {
	}

	public User(String name, String email) {
		this.userId = UUID.randomUUID().toString();
		this.name = name;
		this.email = email;
	}

	public String getUserId() {
		return userId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Override public String toString() {
		return "User{" +
				"userId='" + userId + '\'' +
				", name='" + name + '\'' +
				", email='" + email + '\'' +
				"}";
	}
}
