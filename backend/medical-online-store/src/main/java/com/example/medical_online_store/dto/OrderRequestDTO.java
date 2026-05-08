package com.example.medical_online_store.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.Valid;
import java.util.List;

public class OrderRequestDTO {

    @NotNull(message = "User ID is required")
    private Long userId;

    //Client can pass items when creating the order
    @Valid
    private List<OrderItemRequestDTO> items;


    // Setters
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setItems(List<OrderItemRequestDTO> items) {
        this.items = items;
    }


    // Getters
    public Long getUserId() {
        return userId;
    }

    public List<OrderItemRequestDTO> getItems() {
        return items;
    }



}
