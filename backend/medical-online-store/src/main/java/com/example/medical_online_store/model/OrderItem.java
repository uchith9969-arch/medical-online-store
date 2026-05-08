package com.example.medical_online_store.model;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;



@Entity
@Table(name = "order_items")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)

    private Order order;

    private Long medicineId;

    private Integer quantity;

    private Double unitPrice;

    //Defualt Constructor
    public OrderItem() {}

    //Parameterized Constructor
    public OrderItem(Order order, Long medicineId, Integer quantity, Double unitPrice) {
        this.order = order;
        this.medicineId = medicineId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    //Computed total price for this item
    public Double getTotalPrice(){
        if (quantity != null && unitPrice != null){
            return quantity * unitPrice;
        }
        return 0.0;
    }
    
    //Setters
    public void setOrder(Order order) {
        this.order = order;
    }
 
    public void setMedicineId(Long medicineId) {
        this.medicineId = medicineId;
    }
 
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
 
    public void setUnitPrice(Double unitPrice) {
        this.unitPrice = unitPrice;
    }

    //Getters
    public Long getId() {
        return id;
    }
 
    public Order getOrder() {
        return order;
    }
 
    public Long getMedicineId() {
        return medicineId;
    }
 
    public Integer getQuantity() {
        return quantity;
    }
 
    public Double getUnitPrice() {
        return unitPrice;
    }




    
}

