package com.onofreiflavius.expenseManager.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class UserModel {

    @Id
    private String username;
    private String password;

    public UserModel() {}

    public UserModel(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // Username
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    // Password
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    // To String
    public String toString() { return username + " - " + password; }

}
