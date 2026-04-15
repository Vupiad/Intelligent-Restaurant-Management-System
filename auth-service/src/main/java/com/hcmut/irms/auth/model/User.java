package com.hcmut.irms.auth.model;

import jakarta.persistence.*;

@Entity // Tells Hibernate to make a table out of this class
@Table(name = "users") // Good practice: name the table plural, 'users' instead of 'User'
public class User {

    @Id // Marks this field as the Primary Key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Tells Postgres to auto-increment the ID (1, 2, 3...)
    private Long id;

    @Column(unique = true, nullable = false) // The username must be unique and cannot be empty
    private String username;

    @Column(nullable = false)
    private String password; // We will store the BCrypt hashed password here

    @Enumerated(EnumType.STRING) // Saves the Enum as text ("MANAGER") instead of an integer (0) in the DB
    @Column(nullable = false)
    private Role role;

    // --- Constructors ---
    public User() {
    }

    public User(String username, String password, Role role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    // --- Getters and Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
}
