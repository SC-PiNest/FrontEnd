package com.example.cardviewtest;

public class LoginRequest {
    private String id;
    private String password;

    // Constructor
    public LoginRequest(String id, String password) {
        this.id = id;
        this.password = password;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
