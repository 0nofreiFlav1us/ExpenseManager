package com.onofreiflavius.expenseManager.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "sessions")
public class SessionModel {

    @Id
    private String token;
    private String username;

    public SessionModel() {}

    public SessionModel(String token, String username) {
        this.token = token;
        this.username = username;
    }

    // Token
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    // Username
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    // To String
    public String toString() { return token + " - " + username; }

}
