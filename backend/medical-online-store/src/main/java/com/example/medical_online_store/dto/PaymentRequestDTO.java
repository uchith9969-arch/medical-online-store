package com.example.medical_online_store.dto;

import com.example.medical_online_store.model.PaymentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequestDTO {

    @NotNull(message = "Order ID is required")
    private Long orderId;

    @NotBlank(message = "Patient name is required")
    private String patientName;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private Double amount;

    private PaymentType paymentType; // CASH or CARD

    // Card details (only for CARD payment)
    private String cardHolderName;

    private String cardNumber; // Will be masked to last 4 digits

}