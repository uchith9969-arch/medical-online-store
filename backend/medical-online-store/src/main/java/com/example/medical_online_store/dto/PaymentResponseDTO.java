package com.example.medical_online_store.dto;

import com.example.medical_online_store.model.PaymentStatus;
import com.example.medical_online_store.model.PaymentType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponseDTO {

    private Long id;
    private Long orderId;
    private String patientName;
    private Double amount;
    private PaymentStatus paymentStatus;
    private PaymentType paymentType;
    private String cardHolderName;
    private String cardLastFour;
    private String transactionId;
    private LocalDateTime paymentDate;
    private LocalDateTime createdAt;
}