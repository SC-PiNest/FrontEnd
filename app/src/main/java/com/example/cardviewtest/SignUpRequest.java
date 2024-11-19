package com.example.cardviewtest;

public class SignUpRequest {
    private String id;           // 서버의 id 필드에 매핑
    private String password;     // 서버의 password 필드에 매핑
    private String serialNumber; // 서버의 serialNumber 필드에 매핑

    // 생성자
    public SignUpRequest(String id, String password, String serialNumber) {
        this.id = id;
        this.password = password;
        this.serialNumber = serialNumber;
    }

    // Getter와 Setter
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

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }
}
