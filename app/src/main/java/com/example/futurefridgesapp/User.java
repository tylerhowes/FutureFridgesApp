package com.example.futurefridgesapp;

public class User {
    private String email;
    private String password;
    private String passcode;
    private String role;

    public String getEmail() {
        return email;
    }

    public String getPasscode() {
        return passcode;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setPasscode(String passcode) {
        this.passcode = passcode;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
