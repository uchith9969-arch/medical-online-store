package com.example.medical_online_store.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Payment extends BaseEntity {

    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @Column(name = "patient_name", nullable = false)
    private String patientName;

    @Column(nullable = false)
    private Double amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false)
    private PaymentStatus paymentStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_type")
    private PaymentType paymentType; // CASH or CARD

    @Column(name = "card_holder_name")
    private String cardHolderName;

    @Column(name = "card_last_four")
    private String cardLastFour; // Last 4 digits only for security

    @Column(name = "transaction_id", unique = true)
    private String transactionId;

    @Column(name = "payment_date")
    private LocalDateTime paymentDate;

    // Generate transaction ID before persist
    @PrePersist
    public void generateTransactionId() {
        if (this.transactionId == null) {
            this.transactionId = "TXN-" + System.currentTimeMillis();
        }
    }
}