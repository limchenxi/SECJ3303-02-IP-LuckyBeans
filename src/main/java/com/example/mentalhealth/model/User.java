package com.example.mentalhealth.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "`user`")
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "email", unique = true)     
    private String email;

    @Column(name = "password")  
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = "active")
    private boolean active;

    @Column(name = "created_at")
    private java.time.LocalDateTime createdAt;
    
    public Integer getUserId() {
        return id != null ? id.intValue() : null;
    }
}