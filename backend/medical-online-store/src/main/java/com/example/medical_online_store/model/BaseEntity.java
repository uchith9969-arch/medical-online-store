package com.example.medical_online_store.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Abstract base class for all entities.
 * All entities inherit common fields from this class 
 */

@MappedSuperclass
public abstract class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime createdAt;

    // Called automatically before saving
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    // Abstract method — forces every subclass to provide its own summary
    
    public abstract String getEntitySummary();

    // Setters
    public void setId(Long id) {
        this.id = id;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    // Getters
    public Long getId() {
        return id;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    
}
