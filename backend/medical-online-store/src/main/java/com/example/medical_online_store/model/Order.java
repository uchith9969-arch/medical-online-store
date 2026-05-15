package com.example.medical_online_store.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


 //Order entity — extends BaseEntity
 

@Entity
@Table(name = "orders")
@Inheritance(strategy = InheritanceType.JOINED)
public class Order extends BaseEntity {

    private Long userId;

    private Double totalAmount;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private LocalDateTime orderDate;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    // Constructors
    public Order() {
        this.orderDate = LocalDateTime.now();
        this.status = OrderStatus.PENDING;
        this.totalAmount = 0.0;
    }

    public Order(Long userId) {
        this.userId = userId;
        this.orderDate = LocalDateTime.now();
        this.status = OrderStatus.PENDING;
        this.totalAmount = 0.0;
    }

    // Overrides abstract method from BaseEntity
    @Override
    public String getEntitySummary() {
        return "Order #" + getId() + " | User: " + userId +
               " | Total: LKR " + totalAmount + " | Status: " + status;
    }

    // Returns order type label — overridden in subclasses 
    public String getOrderType() {
        return "REGULAR";
    }

    // Returns calculated priority — overridden in subclasses 
    public int calculatePriority() {
        return 1;
    }

    // Getters
    public Long getUserId() { return userId; }
    public Double getTotalAmount() { return totalAmount; }
    public OrderStatus getStatus() { return status; }
    public LocalDateTime getOrderDate() { return orderDate; }
    public List<OrderItem> getItems() { return items; }

    // Setters
    public void setUserId(Long userId) { this.userId = userId; }
    public void setTotalAmount(Double totalAmount) { this.totalAmount = totalAmount; }
    public void setStatus(OrderStatus status) { this.status = status; }
    public void setOrderDate(LocalDateTime orderDate) { this.orderDate = orderDate; }
    public void setItems(List<OrderItem> items) { this.items = items; }
}