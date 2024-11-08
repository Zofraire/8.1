package com.example.lab81;

public class User {
    public String name;
    public String email;

    // Default constructor for Firebase
    public User() {}

    // Constructor for initializing user data
    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }
}
