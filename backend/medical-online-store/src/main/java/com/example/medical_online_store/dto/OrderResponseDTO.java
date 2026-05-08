package com.example.medical_online_store.dto;

import com.example.medical_online_store.model.OrderStatus;

import java.time.LocalDateTime;

public class OrderResponseDTO {

    private Long id;
    private Long userId;
    private Double totalAmount;
    private OrderStatus status;
    private LocalDateTime orderDate;

    //Constructor
    public OrderResponseDTO(Long id, Long userId, Double totalAmount, OrderStatus status, LocalDateTime orderDate){
        this.id = id;
        this.userId = userId;
        this.totalAmount = totalAmount;
        this.status = status;
        this.orderDate = orderDate;
    
    }

    //Getters
    public Long getId(){
        return id;
    }
    public Long getUserId(){
        return userId;
    }
    public Double getTotalAmount(){
        return totalAmount;
    }
    public OrderStatus getStatus(){
        return status;
    }
    public LocalDateTime getOrderDate(){
        return orderDate;
    }
    


}
