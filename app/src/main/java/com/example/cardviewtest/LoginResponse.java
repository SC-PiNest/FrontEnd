package com.example.cardviewtest;

public class LoginResponse {
    private String status; // "success" or "failure"
    private String message; // 메시지 예: "로그인 성공" 또는 "로그인 실패"

    // Constructor
    public LoginResponse(String status, String message) {
        this.status = status;
        this.message = message;
    }

    // Getters and Setters
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
