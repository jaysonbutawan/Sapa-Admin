package org.example;

public class LoginResult {
    public final boolean success;
    public final String status;
    public final String message;
    public final int userId;

    public LoginResult(boolean success, String status, String message, int userId) {
        this.success = success;
        this.status = status;
        this.message = message;
        this.userId = userId;
    }
}

